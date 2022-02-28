// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.data.constraint.processor.ConstraintContext;
import montithings.services.iot_manager.server.data.constraint.processor.ConstraintPipeline;
import montithings.services.iot_manager.server.distribution.*;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.distribution.suggestion.Suggestion;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Main class for managing the deployment.
 * Software components are deployed to {@link IDeployTargetProvider}s according to a {@link Distribution}.
 * */
public class DeploymentManager implements IDeployStatusListener {
  
  // static configuration
  private final File workingDir;
  private final NetworkInfo network;
  
  // dynamic configuration
  private IDeployTargetProvider targetProvider = new NullDeployTargetProvider();
  
  // current state
  private DeploymentInfo currentDeploymentInfo = null;
  private DeploymentConfiguration currentDeploymentConfig = null;
  private Distribution currentDistribution = null;
  
  // event handling
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  public DeploymentManager(File workingDir, NetworkInfo network) {
    this.workingDir = workingDir;
    this.network = network;
  }
  
  public void setDeploymentInfo(DeploymentInfo info) {
    this.currentDeploymentInfo = info;
  }
  
  public void setDeploymentConfig(DeploymentConfiguration config) {
    this.currentDeploymentConfig = config;
  }
  
  public NetworkInfo getNetworkInfo() {
    return network;
  }
  
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }
  
  /**
   * Terminates the whole deployment, i.e. every component on every deployment
   * target (client).
   */
  public void terminate() {
    if(targetProvider == null)
      return;
    
    try {
      Map<String, String[]> dmap = new HashMap<>(0);
      // deploy nothing to every client
      for(DeployClient client : targetProvider.getClients()) {
        dmap.put(client.getClientID(), new String[0]);
      }
      this.onDeploymentUpdated(new Distribution(dmap));
      targetProvider.deploy(new Distribution(dmap), new DeploymentInfo(), network);
      this.currentDeploymentConfig = null;
      this.currentDeploymentInfo = null;
    }
    catch (DeploymentException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Compute deployment based on {@code currentDeploymentConfig} and
   * {@code currentDeploymentInfo} and perform deployment.
   */
  public void updateDeployment() throws DeploymentException {
    if (this.currentDeploymentConfig == null || this.currentDeploymentInfo == null) {
      return;
    }
    System.out.println("Updating deployment...");
    try {
      // Compute distribution.
      IDistributionCalculator calc = prepareDistributionCalculator(currentDeploymentConfig);
      List<String> instanceNames = this.currentDeploymentInfo.getInstanceNames();
      DistributionCalcRequest request = new DistributionCalcRequest(targetProvider.getClients(), instanceNames);
      request.setReferenceDistribution(currentDistribution);
      this.currentDistribution = calc.computeDistribution(request).exceptionally((t) -> {
        return null;
      }).get();
      
      // Perform deployment.
      if (this.currentDistribution != null) {
        this.onDeploymentUpdated(currentDistribution);
        this.deploy(this.currentDistribution, this.currentDeploymentInfo);
      }
      else {
        throw new DeploymentException("Could not compute a valid deployment");
      }
    }
    catch (Exception e) {
      System.err.println("Failed to update the deployment!");
      throw new DeploymentException(e);
    }
  }
  
  private IDistributionCalculator prepareDistributionCalculator(DeploymentConfiguration config) throws DeploymentException {
    try {
      // Pre-process config.
      System.out.println("XXX BEFORE: "+config.getConstraints());
      ConstraintContext ctx = new ConstraintContext(config, this.targetProvider.getClients());
      config = config.clone();
      ConstraintPipeline.DEFAULT_PIPELINE.apply(ctx, config);
      System.out.println("XXX AFTER: "+config.getConstraints());
      
      // Generate Prolog files.
      IPrologGenerator gen = new RestPrologGenerator();
      String plFacts = gen.generateFacts(targetProvider.getClients()).exceptionally((t) -> {
        return null;
      }).get();
      if (plFacts == null) {
        throw new DeploymentException("Could not generate Prolog facts");
      }
      
      String configStr = new DeployConfigBuilder(config).applyConfigConstraints().build().toString();
      String plQuery = gen.generateQuery(configStr).exceptionally((t) -> {
        return null;
      }).get();
      if (plQuery == null) {
        throw new DeploymentException("Could not generate Prolog query");
      }
      
      return new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
    } catch(DeploymentException e) {
      throw e;
    } catch(Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  public DeploymentConfiguration computeSuggestion(DeploymentConfiguration config, int suggestionIndex) throws DeploymentException {
    try {
      IDistributionCalculator calc = this.prepareDistributionCalculator(config);
      
      List<String> instanceNames = config.getDeploymentInfo().getInstanceNames();
      DistributionSuggestionRequest request = new DistributionSuggestionRequest(targetProvider.getClients(), instanceNames, suggestionIndex, 1);
      
      Map<Distribution, List<Suggestion>> results = calc.computeDistributionSuggestion(request).exceptionally((t) -> {
        t.printStackTrace();
        return null;
      }).get();
      
      if(results == null) {
        throw new DeploymentException("Could not compute a valid suggestion.");
      }
      
      // Clone config so we do not alter the original.
      DeploymentConfiguration cloned = config.clone();
      
      Iterator<Entry<Distribution, List<Suggestion>>> it = results.entrySet().iterator();
      if(it.hasNext()) {
        Entry<Distribution, List<Suggestion>> e = it.next();
        List<Suggestion> suggs = e.getValue();
        // Apply suggestions to cloned config.
        suggs.forEach(s->s.applyTo(cloned));
        System.out.println(suggestionIndex);
        System.out.println(e.getKey());
      }
      
      ConstraintContext ctx = new ConstraintContext(config, targetProvider.getClients());
      ConstraintPipeline.DEFAULT_PIPELINE.clean(ctx, cloned);
      
      return cloned;
    } catch(DeploymentException e) {
      throw e;
    } catch(Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  public boolean validate(DeploymentConfiguration config) throws DeploymentException {
    try {
      IDistributionCalculator calc = this.prepareDistributionCalculator(config);
      
      List<String> instanceNames = config.getDeploymentInfo().getInstanceNames();
      DistributionCalcRequest request = new DistributionCalcRequest(targetProvider.getClients(), instanceNames);
      Distribution distribution = calc.computeDistribution(request).exceptionally((t) -> {
        return null;
      }).get();
      
      return distribution != null;
    } catch(DeploymentException e) {
      throw e;
    } catch(Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  private void deploy(Distribution distribution, DeploymentInfo deploymentInfo) throws DeploymentException {
    targetProvider.deploy(distribution, deploymentInfo, network);
  }
  
  public void setTargetProvider(IDeployTargetProvider provider) {
    // Close old target provider
    try {
      this.terminate();
      this.targetProvider.close();
    } catch(DeploymentException e) {
      e.printStackTrace();
    }
    
    // Replace with new one
    this.targetProvider = provider;
    this.targetProvider.setStatusListener(this);
    
    // Initialize new target provider
    try {
      provider.initialize();
    } catch(DeploymentException e) {
      System.err.println("Failed to initialize deployment provider.");
    }
    
    // send device update to listener
    for(DeployClient client : targetProvider.getClients()) {
      if(client.isOnline()) this.listener.onClientOnline(client);
      else this.listener.onClientOffline(client);
    }
    
    System.out.println("Changed deployment target provider.");
    
    // update deployment
    try {
      this.updateDeployment();
    }
    catch (DeploymentException e) {
      e.printStackTrace();
    }
  }
  
  public IDeployTargetProvider getTargetProvider() {
    return this.targetProvider;
  }
  
  @Override
  public void onClientOnline(DeployClient client) {
    this.listener.onClientOnline(client);
    try {
      this.updateDeployment();
    }
    catch (DeploymentException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onClientOffline(DeployClient client) {
    this.listener.onClientOffline(client);
    try {
      this.updateDeployment();
    }
    catch (DeploymentException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDeploymentUpdated(Distribution distribution) {
    this.listener.onDeploymentUpdated(distribution);
  }
  
}

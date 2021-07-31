package ps.deployment.server;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.data.constraint.processor.ConstraintContext;
import ps.deployment.server.data.constraint.processor.ConstraintPipeline;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.DistributionSuggestionRequest;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;
import ps.deployment.server.distribution.config.DeployConfigBuilder;
import ps.deployment.server.distribution.listener.IDeployStatusListener;
import ps.deployment.server.distribution.listener.VoidDeployStatusListener;
import ps.deployment.server.distribution.suggestion.Suggestion;
import ps.deployment.server.exception.DeploymentException;

public class DeploymentManager implements IDeployStatusListener {
  
  // static configuration
  private final File workingDir;
  private final NetworkInfo network;
  
  // dynamic configuration
  private IDeployTargetProvider targetProvider = null;
  
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
      targetProvider.deploy(new Distribution(new HashMap<>(0)), new DeploymentInfo(), network);
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
      this.currentDistribution = calc.computeDistribution(targetProvider.getClients(), instanceNames).exceptionally((t) -> {
        return null;
      }).get();
      
      // Perform deployment.
      if (this.currentDistribution != null) {
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
      DistributionSuggestionRequest request = new DistributionSuggestionRequest(targetProvider.getClients(), instanceNames, 0, 10);
      
      Map<Distribution, List<Suggestion>> results = calc.computeDistributionSuggestion(request).exceptionally((t) -> {
        return null;
      }).get();
      
      if(results == null) {
        throw new DeploymentException("Could not compute a valid suggestion.");
      }
      
      // Clone config so we do not alter the original.
      DeploymentConfiguration cloned = config.clone();
      
      Iterator<Entry<Distribution, List<Suggestion>>> it = results.entrySet().iterator();
      int index = 0;
      while(it.hasNext()) {
        Entry<Distribution, List<Suggestion>> e = it.next();
        if(index == suggestionIndex) {
          List<Suggestion> suggs = e.getValue();
          // Apply suggestions to cloned config.
          suggs.forEach(s->s.applyTo(cloned));
          break;
        }
        index++;
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
      Distribution distribution = calc.computeDistribution(targetProvider.getClients(), instanceNames).exceptionally((t) -> {
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
    if (this.targetProvider != provider) {
      terminate();
    }
    this.targetProvider = provider;
    this.targetProvider.setStatusListener(this);
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
  
}

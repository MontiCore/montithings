package ps.deployment.server;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.DefaultDistributionCalculator;
import ps.deployment.server.distribution.DistributionSuggestionRequest;
import ps.deployment.server.distribution.IDistributionCalculator;
import ps.deployment.server.distribution.IPrologGenerator;
import ps.deployment.server.distribution.RestPrologGenerator;
import ps.deployment.server.distribution.config.DeployConfigBuilder;
import ps.deployment.server.distribution.config.DockerComposeConfig;
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
    
    for (DeployClient client : targetProvider.getClients()) {
      try {
        targetProvider.deploy(client.getClientID(), null);
      }
      catch (DeploymentException e) {
        e.printStackTrace();
      }
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
      // Generate Prolog files.
      IPrologGenerator gen = new RestPrologGenerator();
      String plFacts = gen.generateFacts(targetProvider.getClients()).exceptionally((t) -> {
        return null;
      }).get();
      if (plFacts == null) {
        throw new DeploymentException("Could not generate Prolog facts");
      }
      
      String configStr = new DeployConfigBuilder(currentDeploymentConfig).applyConfigConstraints().build().toString();
      String plQuery = gen.generateQuery(configStr).exceptionally((t) -> {
        return null;
      }).get();
      if (plQuery == null) {
        throw new DeploymentException("Could not generate Prolog query");
      }
      
      // Compute distribution.
      IDistributionCalculator calc = new DefaultDistributionCalculator(plFacts, plQuery, workingDir);
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
      throw new DeploymentException(e);
    }
  }
  
  private IDistributionCalculator prepareDistributionCalculator(DeploymentConfiguration config) throws DeploymentException {
    try {
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
      
      // Clone config so we do not alter the original.
      DeploymentConfiguration cloned = config.clone();
      
      Iterator<Entry<Distribution, List<Suggestion>>> it = results.entrySet().iterator();
      int index = 0;
      while(it.hasNext()) {
        Entry<Distribution, List<Suggestion>> e = it.next();
        if(index == suggestionIndex) {
          List<Suggestion> suggs = e.getValue();
          suggs.forEach(s->s.applyTo(cloned));
          break;
        }
        index++;
      }
      
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
    Map<String, DockerComposeConfig> composes = DockerComposeConfig.fromDistribution(distribution, deploymentInfo, network);
    for (Entry<String, DockerComposeConfig> e : composes.entrySet()) {
      targetProvider.deploy(e.getKey(), e.getValue().serializeYaml());
    }
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

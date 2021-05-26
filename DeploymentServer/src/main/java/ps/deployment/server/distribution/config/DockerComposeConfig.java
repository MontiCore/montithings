package ps.deployment.server.distribution.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Tag;

import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.InstanceInfo;
import ps.deployment.server.data.NetworkInfo;

public class DockerComposeConfig {
  
  private static final String version = "3.7";
  private Map<String, DockerComposeService> services = new HashMap<>();
  
  public DockerComposeConfig() {
    
  }
  
  public void addService(String name, DockerComposeService service) {
    services.put(name, service);
  }
  
  public Map<String, DockerComposeService> getServices() {
    return services;
  }
  
  public String getVersion() {
    return version;
  }
  
  public String serializeYaml() {
    DumperOptions opt = new DumperOptions();
    opt.setAllowReadOnlyProperties(true);
    opt.setDefaultFlowStyle(FlowStyle.BLOCK);
    
    // hide java class name
    Representer repr = new Representer(opt);
    repr.addClassTag(DockerComposeConfig.class, Tag.MAP);
    
    return new Yaml(repr, opt).dump(this);
  }
  
  
  public static Map<String, DockerComposeConfig> fromDistribution(Distribution distribution, DeploymentInfo deploy, NetworkInfo net) {
    // index instance info for efficient access
    HashMap<String, InstanceInfo> instanceInfos = new HashMap<>();
    for(InstanceInfo info : deploy.getInstances()) {
      instanceInfos.put(info.getInstanceName(), info);
    }
    
    // defaults
    
    HashMap<String, DockerComposeConfig> res = new HashMap<>();
    
    // construct config for each client
    for(Entry<String,String[]> e : distribution.getDistributionMap().entrySet()) {
      String clientID = e.getKey();
      DockerComposeConfig config = new DockerComposeConfig();
      
      // include a service for each instance that should be run on this client
      for(String instanceName : e.getValue()) {
        InstanceInfo instance = instanceInfos.get(instanceName);
        if(instance == null) {
          throw new RuntimeException("Found invalid instance name in distribution!");
        }
        String dockerImage = net.getDockerRepositoryPrefix()+instance.getComponentType().toLowerCase();
        DockerComposeService service = new DockerComposeService(dockerImage, instance.getInstanceName(), net.getMqttHost(), net.getMqttPort());
        config.addService(instanceName, service);
      }
      
      res.put(clientID, config);
    }
    return res;
  }
  
}

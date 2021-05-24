package ps.deployment.server.distribution.config;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class DockerComposeConfig {
  
  private Map<String, DockerComposeService> services = new HashMap<>();
  
  public DockerComposeConfig() {
    
  }
  
  public void addService(String name, DockerComposeService service) {
    services.put(name, service);
  }
  
  public Map<String, DockerComposeService> getServices() {
    return services;
  }
  
  public String serializeYaml() {
    DumperOptions opt = new DumperOptions();
    opt.setAllowReadOnlyProperties(true);
    opt.setPrettyFlow(true);
    opt.setDefaultFlowStyle(FlowStyle.BLOCK);
    return new Yaml(opt).dump(this);
  }
  
}

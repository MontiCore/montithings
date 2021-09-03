package ps.deployment.server.distribution.config;

import ps.deployment.server.util.MontiThingsUtil;

public class DockerComposeService {
  
  private String image;
  private String command;
  private String network_mode = "host";
  private String restart = "always";
  
  // On some systems, MQTT will fail to connect to the broker when using
  // cross-compiled images. This is related to the MUSL linker and unavailable
  // SYSCALLs on e.g. ARM architectures. Giving the container privileged rights
  // will prevent this from happening.
  private final boolean privileged = true;
  
  public DockerComposeService() {
    
  }
  
  public DockerComposeService(String image, String instanceName, String mqttHost, int mqttPort) {
    this.image = image;
    this.command = MontiThingsUtil.getRunArgumentsAsString(instanceName, mqttHost, mqttPort);
  }
  
  public String getImage() {
    return image;
  }
  
  public String getCommand() {
    return command;
  }
  
  public String getNetwork_mode() {
    return network_mode;
  }
  
  public String getRestart() {
    return restart;
  }
  
  public void setImage(String image) {
    this.image = image;
  }
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public void setNetwork_mode(String network_mode) {
    this.network_mode = network_mode;
  }
  
  public void setRestart(String restart) {
    this.restart = restart;
  }
  
  public boolean isPrivileged() {
    return this.privileged;
  }
  
}

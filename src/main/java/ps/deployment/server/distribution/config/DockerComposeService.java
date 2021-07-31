package ps.deployment.server.distribution.config;

import ps.deployment.server.util.MontiThingsUtil;

public class DockerComposeService {
  
  private String image;
  private String command;
  private String network_mode = "host";
  private String restart = "always";
  
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
  
}

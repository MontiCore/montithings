// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

public class NetworkInfo {
  
  private String mqttHost = "127.0.0.1";
  private int mqttPort = 1883;
  private String dockerRepositoryPrefix = ""; //"localhost:5000/";
  private String dockerRepositoryUsername = "";
  private String dockerRepositoryPassword = "";

  public String getMqttHost() {
    return mqttHost;
  }
  
  public void setMqttHost(String mqttHost) {
    this.mqttHost = mqttHost;
  }
  
  public int getMqttPort() {
    return mqttPort;
  }
  
  public void setMqttPort(int mqttPort) {
    this.mqttPort = mqttPort;
  }
  
  public String getDockerRepositoryPrefix() {
    return dockerRepositoryPrefix;
  }

  public String getDockerRepositoryUsername() {
    return dockerRepositoryUsername;
  }

  public void setDockerRepositoryUsername(String dockerRepositoryUsername) {
    this.dockerRepositoryUsername = dockerRepositoryUsername;
  }

  public String getDockerRepositoryPassword() {
    return dockerRepositoryPassword;
  }

  public void setDockerRepositoryPassword(String dockerRepositoryPassword) {
    this.dockerRepositoryPassword = dockerRepositoryPassword;
  }

  public void setDockerRepositoryPrefix(String dockerRepository) {
    if(!dockerRepository.endsWith("/")) dockerRepository += "/";
    this.dockerRepositoryPrefix = dockerRepository;
  }
  
  public String getMqttURI() {
    return "tcp://"+this.getMqttHost() + ":" + this.getMqttPort();
  }
  
}

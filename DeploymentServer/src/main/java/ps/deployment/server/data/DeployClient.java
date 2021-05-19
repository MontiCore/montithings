package ps.deployment.server.data;

public class DeployClient {
  
  private String clientID;
  private boolean online;
  private DeployClientLocation location;
  private String[] hardware;
  
  public String getClientID() {
    return clientID;
  }
  
  public void setClientID(String clientID) {
    this.clientID = clientID;
  }
  
  public boolean isOnline() {
    return online;
  }
  
  public void setOnline(boolean online) {
    this.online = online;
  }
  
  public DeployClientLocation getLocation() {
    return location;
  }
  
  public void setLocation(DeployClientLocation location) {
    this.location = location;
  }
  
  public String[] getHardware() {
    return hardware;
  }
  
  public void setHardware(String[] hardware) {
    this.hardware = hardware;
  }
  
  public static DeployClient create(String clientID, boolean online, DeployClientLocation location, String... hardware) {
    DeployClient client = new DeployClient();
    client.setClientID(clientID);
    client.setOnline(online);
    client.setLocation(location);
    client.setHardware(hardware);
    return client;
  }
  
}

package ps.deployment.server.data;

public class DeployClient {
  
  private String clientID;
  private boolean online;
  private DeployClientLocation location;
  private String[] hardware;
  private long lastSeen;
  
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
  
  /**
   * @return The time-stamp of the last time this client was seen (e.g. heart beat).  
   * */
  public long getLastSeen() {
    return lastSeen;
  }
  
  public void setLastSeen(long lastSeen) {
    this.lastSeen = lastSeen;
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

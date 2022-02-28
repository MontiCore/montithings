// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

/**
 * An instance of {@link DeployClient} is considered a host
 * device (physical or logical) that one can deploy software to.
 */
public class DeployClient {
  
  private String clientID;
  private boolean online;
  private LocationSpecifier location = new LocationSpecifier();
  private String[] hardware = new String[0];
  private long lastSeen;
  private long targetProviderID;
  
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
  
  public LocationSpecifier getLocation() {
    return location;
  }
  
  public void setLocation(LocationSpecifier location) {
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
  
  public static DeployClient create(String clientID, boolean online, LocationSpecifier location, long targetProviderID, String... hardware) {
    DeployClient client = new DeployClient();
    client.setClientID(clientID);
    client.setOnline(online);
    client.setLocation(location);
    client.setHardware(hardware);
    client.setTargetProviderID(targetProviderID);
    return client;
  }

  public long getTargetProviderID() {
    return targetProviderID;
  }

  public void setTargetProviderID(long targetProviderID) {
    this.targetProviderID = targetProviderID;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    DeployClient client = (DeployClient) o;

    return clientID != null ? clientID.equals(client.clientID) : client.clientID == null;
  }

  @Override public int hashCode() {
    return clientID != null ? clientID.hashCode() : 0;
  }
}

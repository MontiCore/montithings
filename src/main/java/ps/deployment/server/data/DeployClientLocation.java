package ps.deployment.server.data;

public class DeployClientLocation {
  
  private String building, floor, room;
  
  public String getBuilding() {
    return building;
  }
  
  public void setBuilding(String building) {
    this.building = building;
  }
  
  public String getFloor() {
    return floor;
  }
  
  public void setFloor(String floor) {
    this.floor = floor;
  }
  
  public String getRoom() {
    return room;
  }
  
  public void setRoom(String room) {
    this.room = room;
  }
  
  @Override
  public String toString() {
    return "DeployClientLocation [building=" + building + ", floor=" + floor + ", room=" + room + "]";
  }
  
  public static DeployClientLocation create(String building, String floor, String room) {
    DeployClientLocation loc = new DeployClientLocation();
    loc.setBuilding(building);
    loc.setFloor(floor);
    loc.setRoom(room);
    return loc;
  }
  
}

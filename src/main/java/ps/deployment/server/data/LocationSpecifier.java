package ps.deployment.server.data;

public class LocationSpecifier {
  
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
    return "LocationSpecifier [building=" + building + ", floor=" + floor + ", room=" + room + "]";
  }
  
  /**
   * Constructs a String like "building1_floor2_room3"
   * @return this String
   * */
  public String toPrologString() {
    StringBuilder sb = new StringBuilder();
    if(building != null && !building.isEmpty()) {
      sb.append("building").append(building);
    }
    if(floor != null && !floor.isEmpty()) {
      if(sb.length() > 0) sb.append('_');
      sb.append("floor").append(floor);
    }
    if(room != null && !room.isEmpty()) {
      if(sb.length() > 0) sb.append('_');
      sb.append("room").append(room);
    }
    return sb.toString();
  }
  
  public static LocationSpecifier create(String building, String floor, String room) {
    LocationSpecifier loc = new LocationSpecifier();
    loc.setBuilding(building);
    loc.setFloor(floor);
    loc.setRoom(room);
    return loc;
  }
  
}

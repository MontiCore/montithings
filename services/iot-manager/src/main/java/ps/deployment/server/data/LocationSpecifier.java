// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.data;

public class LocationSpecifier {
  
  private String building, floor, room;
  
  public LocationSpecifier() {
    
  }
  
  public LocationSpecifier(String building, String floor, String room) {
    this.building = building;
    this.floor = floor;
    this.room = room;
  }
  
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
  
  /**@return whether this LocationSpecifier is fully specified, i.e. whether building, floor and room are all set.*/
  public boolean isFullySpecified() {
    return this.building != null && this.floor != null && this.room != null;
  }
  
  @Override
  public String toString() {
    return "LocationSpecifier [building=" + building + ", floor=" + floor + ", room=" + room + "]";
  }
  
  /**
   * Constructs a String like "building1_floor2_room3"
   * 
   * @return this String
   */
  public String toPrologString() {
    StringBuilder sb = new StringBuilder();
    if (building != null && !building.isEmpty()) {
      sb.append("building").append(building);
    }
    if (floor != null && !floor.isEmpty()) {
      if (sb.length() > 0)
        sb.append('_');
      sb.append("floor").append(floor);
    }
    if (room != null && !room.isEmpty()) {
      if (sb.length() > 0)
        sb.append('_');
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
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((building == null) ? 0 : building.hashCode());
    result = prime * result + ((floor == null) ? 0 : floor.hashCode());
    result = prime * result + ((room == null) ? 0 : room.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LocationSpecifier other = (LocationSpecifier) obj;
    if (building == null) {
      if (other.building != null)
        return false;
    }
    else if (!building.equals(other.building))
      return false;
    if (floor == null) {
      if (other.floor != null)
        return false;
    }
    else if (!floor.equals(other.floor))
      return false;
    if (room == null) {
      if (other.room != null)
        return false;
    }
    else if (!room.equals(other.room))
      return false;
    return true;
  }
  
}

package ps.deployment.server.data;

public class InstanceInfo {
  
  private String componentType, instanceName;
  private String[] requirements;
  
  public InstanceInfo(String componentType, String instanceName, String... requirements) {
    this.componentType = componentType;
    this.instanceName = instanceName;
    this.requirements = requirements;
  }
  
  public String getComponentType() {
    return componentType;
  }
  
  public void setComponentType(String componentType) {
    this.componentType = componentType;
  }
  
  public String getInstanceName() {
    return instanceName;
  }
  
  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }
  
  public String[] getRequirements() {
    return requirements;
  }
  
  public void setRequirements(String[] requirements) {
    this.requirements = requirements;
  }
  
}

// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

public class InstanceInfo {

  private String componentType, instanceName;
  private String[] requirements;
  private TerraformInfo[] terraformInfo;

  private String hardwareRequirement;

  public InstanceInfo(String componentType, String instanceName, String hardwareRequirement, String[] requirements,
      TerraformInfo[] terraformInfo) {
    this.componentType = componentType;
    this.instanceName = instanceName;
    this.hardwareRequirement = hardwareRequirement;
    this.requirements = requirements;
    this.terraformInfo = terraformInfo;
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

  public String getHardwareRequirement() {
    return hardwareRequirement;
  }

  public void setHardwareRequirement(String hardwareRequirement) {
    this.hardwareRequirement = hardwareRequirement;
  }

  public String[] getRequirements() {
    return requirements;
  }

  public void setRequirements(String[] requirements) {
    this.requirements = requirements;
  }

  public TerraformInfo[] getTerraformInfo() {
    return terraformInfo;
  }

  public void setTerraformInfo(TerraformInfo[] terraformInfo) {
    this.terraformInfo = terraformInfo;
  }

}

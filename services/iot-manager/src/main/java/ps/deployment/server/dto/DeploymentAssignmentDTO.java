// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DeploymentAssignmentDTO {
  
  protected String target = "";
  protected List<String> components = new LinkedList<>();
  
  public DeploymentAssignmentDTO() {
    
  }
  
  public DeploymentAssignmentDTO(String target, String... targets) {
    this.target = target;
    this.components = Arrays.asList(targets);
  }
  
  public String getTarget() {
    return target;
  }
  
  public void setTarget(String target) {
    this.target = target;
  }
  
  public List<String> getComponents() {
    return components;
  }
  
  public void setComponents(List<String> components) {
    this.components = components;
  }
  
}

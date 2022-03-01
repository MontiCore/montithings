// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.genesis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for modifying a GENESIS deployment model. The model is kept in its
 * parsed JSON-form to allow for better forward compatibility (instead of parsing it to
 * a fixed data structure).
 */
public class GenesisDeployModel {
  
  private static String DEFAULT_EXECUTION_PORT_SOFTWARE = "softwareExecPort";
  private static String DEFAULT_SSH_RESOURCE = "ssh";
  
  private JsonObject model;
  
  /**
   * @param jsonModel The parsed model as received form the GeneSIS server.
   * */
  public GenesisDeployModel(JsonObject jsonModel) {
    this.model = jsonModel;
  }
  
  public JsonObject getModel() {
    return this.model;
  }
  
  /**
   * @return List of resources with type "/infra/device".
   * @throws DeploymentException when the model is invalid
   * */
  protected List<JsonObject> getDeviceResources() throws DeploymentException {
    return getComponentsOfType("/infra/device");
  }
  
  /**
   * Extracts all components of given type from the model.
   * @return List of resources with given type form the underlying deployment model.
   * @throws DeploymentException when the model is invalid
   * */
  protected List<JsonObject> getComponentsOfType(String type) throws DeploymentException {
    List<JsonObject> resources = new ArrayList<JsonObject>();
    JsonArray jComponents = model.get("components").getAsJsonArray();
    for(JsonElement je : jComponents) {
      if(!je.isJsonObject()) throw new DeploymentException("Invalid GeneSIS deployment model");
      JsonObject jResource = je.getAsJsonObject();
      if(type.equals(jResource.get("_type").getAsString())) {
        resources.add(jResource);
      }
    }
    return resources;
  }
  
  /**
   * @return a list of all ssh execution port identifiers 
   * @throws DeploymentException when the model is invalid
   * */
  public List<String> getHostPortsSSH() throws DeploymentException {
    List<String> identifiers = new ArrayList<>();
    for(JsonObject jDevice : getDeviceResources()) {
      JsonArray jExePorts = jDevice.get("provided_execution_port").getAsJsonArray();
      
      // A device can have multiple execution ports. We only need one.
      JsonObject jExecPort = jExePorts.isJsonObject() ? jExePorts.getAsJsonObject() : jExePorts.getAsJsonArray().get(0).getAsJsonObject();
      
      // form full qualified execution port name from device & port name
      String id = "/" + jDevice.get("name").getAsString() + "/" + jExecPort.get("name").getAsString();
      
      // add each port identifier
      identifiers.add(id);
    }
    return identifiers;
  }
  
  protected JsonObject getHostComponent(String hostComponentName) {
    JsonArray jComponents = model.get("components").getAsJsonArray();
    for(JsonElement jeComp : jComponents) {
      JsonObject jComp = jeComp.getAsJsonObject();
      if(hostComponentName.equals(jComp.get("name").getAsString())) {
        return jComp;
      }
    }
    return null;
  }
  
  /**
   * @return the credentials JsonObject of the host with the given execution port.
   * @throws DeploymentException if the model is invalid or there are no credentials for this host.
   * */
  protected JsonObject getCredentailsForSSHHost(String hostComponentName) throws DeploymentException {
    JsonObject comp = getHostComponent(hostComponentName);
    if(comp != null) {
      return comp.getAsJsonObject("credentials").getAsJsonObject();
    } else {
      throw new DeploymentException("There are no SSH credentials given for GeneSIS host \""+hostComponentName+"\"");
    }
  }
  
  protected String getDeviceTypeOfHost(String hostComponentName) {
    JsonObject jHost = getHostComponent(hostComponentName);
    if(jHost != null) {
      JsonElement jeProps = jHost.get("device_type");
      if(jeProps != null && jeProps.isJsonPrimitive()) {
        return jeProps.getAsString();
      }
    }
    return "";
  }
  
  /**
   * Adds a containment to the model, i.e. the instance for componentPortId is to be deployed on the host with providerPortId.
   * */
  protected void addContainment(String containmentName, String providerPortId, String componentPortId) {
    JsonObject jCon = new JsonObject();
    jCon.addProperty("name", containmentName);
    jCon.addProperty("src", providerPortId);
    jCon.addProperty("target", componentPortId);
    jCon.add("properties", new JsonArray());
    model.get("containments").getAsJsonArray().add(jCon);
  }
  
  /**
   * Adds a new software resource for deployment on SSH host.
   * @param name Name of the new resource
   * @param version Version of the new resource
   * @param credentials Credentials of the SSH host
   * */
  protected void addSSHResource(String name, String version, String cmdDownload, String cmdInstall, String cmdConfigure, String cmdStart, String cmdStop, JsonObject credentials) {
    // Define required execution port 
    JsonObject reqExecPort = new JsonObject();
    reqExecPort.addProperty("name", DEFAULT_EXECUTION_PORT_SOFTWARE);
    
    // Define SSH resource
    JsonObject sshResource = new JsonObject();
    sshResource.addProperty("name", DEFAULT_SSH_RESOURCE);
    sshResource.addProperty("downloadCommand", cmdDownload);
    sshResource.addProperty("installCommand", cmdInstall);
    sshResource.addProperty("configureCommand", cmdConfigure);
    sshResource.addProperty("startCommand", cmdStart);
    sshResource.addProperty("stopCommand", cmdStop);
    sshResource.add("uploadCommand", new JsonArray());
    sshResource.add("credentials", credentials);
    
    // Define GeneSIS software component
    JsonObject jComp = new JsonObject();
    jComp.addProperty("_type", "/internal");
    jComp.addProperty("name", name);
    jComp.addProperty("version", version);
    jComp.addProperty("version", version);
    jComp.add("required_execution_port", reqExecPort);
    jComp.add("ssh_resource", sshResource);
    
    // We do not make use of the following, but still have to define them:
    jComp.add("provided_execution_port", new JsonArray());
    jComp.add("provided_communication_port", new JsonArray());
    jComp.add("required_communication_port", new JsonArray());
    jComp.add("properties", new JsonArray());
    
    // Add it to the model
    this.model.get("components").getAsJsonArray().add(jComp);
  }
  
  /**
   * Removes all containments from the model.
   * */
  public void clearContainments() {
    this.model.add("containments", new JsonArray());
  }
  
  public void clearSoftwareComponents() {
    JsonArray jComps = this.model.get("components").getAsJsonArray();
    JsonArray newComps = new JsonArray();
    // filter components from jComps to newComps
    for(JsonElement jeComp : jComps) {
      if(jeComp.isJsonObject()) {
        JsonObject jComp = jeComp.getAsJsonObject();
        if(!"/internal".equals(jComp.get("_type").getAsString())) {
          newComps.add(jComp);
        }
      }
    }
    // replace with filtered version
    this.model.add("components", newComps);
  }
  
protected String extractHost(String fullyQualifiedExecutionPort) {
    // fully qualified execution ports have the format of "/hostName/portName"
    return fullyQualifiedExecutionPort.split("/")[1];
  }
  
  /**
   * Adds a MontiThings application to be deployed on {@code hostExecPort}.
   * */
  public void addMontiThingsDeployment(String dockerImage, String montiThingsArgs, String hostExecPort) throws DeploymentException {
    JsonObject credentials = getCredentailsForSSHHost(extractHost(hostExecPort));
    String resourceName = UUID.randomUUID().toString();
    String cmdDownload = "docker pull \""+dockerImage+"\"";
    
    // We'll reuse the resourceName as the container name for docker.
    String cmdStart = "docker run -d --privileged --rm -it --name \""+resourceName+"\" \""+dockerImage+"\" " + montiThingsArgs;
    String cmdStop = "docker stop \""+resourceName+"\" || true";
    
    // Add SSH resource & assign it to the given host execution port
    addSSHResource(resourceName, "0.0.1", cmdDownload, "", "", cmdStart, cmdStop, credentials);
    addContainment(UUID.randomUUID().toString(), hostExecPort, "/"+resourceName+"/"+DEFAULT_EXECUTION_PORT_SOFTWARE);
  }
  
  
  
}

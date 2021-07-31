package ps.deployment.server.k8s;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LocalObjectReference;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeCondition;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1NodeStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.util.CallGeneratorParams;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import ps.deployment.server.IDeployTargetProvider;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.InstanceInfo;
import ps.deployment.server.data.LocationSpecifier;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.listener.IDeployStatusListener;
import ps.deployment.server.distribution.listener.VoidDeployStatusListener;
import ps.deployment.server.exception.DeploymentException;
import ps.deployment.server.util.MontiThingsUtil;

public class K8sDeployTargetProvider implements IDeployTargetProvider, ResourceEventHandler<V1Node> {
  
  public static void main(String[] args) throws IOException, ApiException {
    String hostURL = "https://localhost:6443";
    String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImpvRXFtY2Q3ZHJ3Q001OElWNXI1ME1vRElrUmw5eElFd0dCMk83a3VFMkkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImlvdC10b2tlbi1jOXJyaCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJpb3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI1ZmY3Zjc5Zi0xNjQxLTRiYTctOGZjNC03MDIzMDVkNWEzZjEiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDppb3QifQ.STCH2LLFlyhTXd5mA1IppKW2-mYUBpwNdQm3QxEP3qgMKJNK0vBJqgD26861CqFo8UwHAom4ZBj3a_jnvazVakYLWUz-qR-9Wzvco8li3yNfZWRCZR5QAUUX2drnajtLIf8-CErw282Y4UPZrGrSWKBJfYOG_CMW_ZVwDE1aAQFw9nfDFj2TZb7CyL5WccoUVKptqsJYkUqLjcV0d3rzuFyquXMdCWVp5tvyys8KU1f3he8uuLYYXGJhqZ3OziVmULX0SA1dWU7VJ9sFjJknsyfl_Q8X0HQ6Lb9j_QkUOPh_PntrVR56Oyw6-C5KBQRx3p-wVyvjLfeUUuQQgtlwfw";
    new K8sDeployTargetProvider(Config.fromToken(hostURL, token, false));
  }
  
  private static final String LITERAL_NODE_READY = "Ready";
  private static final String PREFIX_CLIENTID = "k8s-";
  
  private final ApiClient client;
  private final CoreV1Api apiCore;
  private final AppsV1Api apiApps;
  
  private final Map<V1Node, K8sDeployClient> clients = new HashMap<>();
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  public K8sDeployTargetProvider(String hostURL, String token) throws IOException {
    this(Config.fromToken(hostURL, token, false));
  }
  
  public K8sDeployTargetProvider(ApiClient client) throws IOException {
    this.client = client;
    this.apiCore = new CoreV1Api(client);
    this.apiApps = new AppsV1Api(client);
    
    this.startListening();
  }
  
  private void startListening() {
    SharedInformerFactory factory = new SharedInformerFactory(this.client);
    SharedIndexInformer<V1Node> nodeInformer = factory.sharedIndexInformerFor((CallGeneratorParams params) -> {
      return this.apiCore.listNodeCall(null, null, null, null, null, null, params.resourceVersion, null, params.timeoutSeconds, params.watch, null);
    }, V1Node.class, V1NodeList.class);
    nodeInformer.addEventHandler(this);
    factory.startAllRegisteredInformers();
  }
  
  /**
   * Creates a {@link K8sDeployClient} for the given node if the node is
   * properly configured. A node is properly configured if its location is fully
   * specified.
   */
  private K8sDeployClient createClientForNode(V1Node node) {
    K8sDeployClient dc = new K8sDeployClient();
    this.updateClientWithNode(dc, node, false);
    if (!dc.getLocation().isFullySpecified()) {
      return null;
    }
    return dc;
  }
  
  private K8sDeployClient updateClientWithNode(K8sDeployClient dc, V1Node node) {
    return this.updateClientWithNode(dc, node, true);
  }
  
  /**
   * Update the {@link K8sDeployClient} object with the data from {@code node}
   * if it is properly configured (or when forced).
   */
  private K8sDeployClient updateClientWithNode(K8sDeployClient dc, V1Node node, boolean force) {
    V1ObjectMeta meta = node.getMetadata();
    
    boolean wasOnline = dc.isOnline();
    
    // Extract whether the node is ready.
    // The deploy client is considered online iff the node is ready.
    V1NodeStatus status = node.getStatus();
    boolean online = false;
    for (V1NodeCondition condition : status.getConditions()) {
      if (LITERAL_NODE_READY.equals(condition.getType())) {
        online = Boolean.TRUE.toString().equalsIgnoreCase(condition.getStatus());
      }
    }
    
    LocationSpecifier loc = this.getLocationSpecifierForNode(node);
    if (loc.isFullySpecified() || force) {
      dc.setLocation(loc);
      dc.setClientID(PREFIX_CLIENTID + meta.getName());
      dc.setOnline(online);
      dc.setHardware(new String[0]);
      if (online)
        dc.setLastSeen(System.currentTimeMillis());
      
      if (!wasOnline && online) {
        // if the client was not online before, but is now, call the listener.
        this.listener.onClientOnline(dc);
      }
      
      if (wasOnline && !online) {
        // if the client was online before, but isn't anymore, call the
        // listener.
        this.listener.onClientOffline(dc);
      }
      
      // TODO implement hardware info from k8s nodes
      // TODO remove stub impl:
      dc.setHardware(new String[] { "sensorTemperature", "actuatorTemperature" });
    }
    
    return dc;
  }
  
  private LocationSpecifier getLocationSpecifierForNode(V1Node node) {
    V1ObjectMeta meta = node.getMetadata();
    Map<String, String> labels = meta.getLabels();
    
    String building = labels.get("building");
    String floor = labels.get("floor");
    String room = labels.get("room");
    
    return new LocationSpecifier(building, floor, room);
  }
  
  private K8sDeployClient getClientWithID(String clientID) {
    for (K8sDeployClient client : this.clients.values()) {
      if (client.getClientID().equals(clientID))
        return client;
    }
    return null;
  }
  
  /** removes the ClientID-prefix if present */
  private String transformClientID(String clientID) {
    if(clientID.startsWith(PREFIX_CLIENTID)) {
      return clientID.substring(PREFIX_CLIENTID.length());
    }
    return clientID;
  }
  
  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException {
    // index instance info for efficient access
    HashMap<String, InstanceInfo> instanceInfos = new HashMap<>();
    for(InstanceInfo info : deploymentInfo.getInstances()) {
      instanceInfos.put(info.getInstanceName(), info);
    }
    
    for(Entry<String, String[]> e : distribution.getDistributionMap().entrySet()) {
      String clientID = e.getKey();
      String[] instances = e.getValue();
      
      K8sDeployClient client = getClientWithID(clientID);
      // Check whether this client is owned by this deploy target provider.
      if(client != null) {
        String k8sClientID = transformClientID(clientID);
        
        V1PodSpec podSpec = new V1PodSpec()
            .nodeName(k8sClientID)
            .hostNetwork(true)
            .restartPolicy("Always")
            .addImagePullSecretsItem(new V1LocalObjectReference().name("regcred"));
        
        // Add a container for each application instance.
        for(String instanceName : instances) {
          InstanceInfo instance = instanceInfos.get(instanceName);
          if(instance == null) {
            throw new RuntimeException("Found invalid instance name in distribution!");
          }
          
          V1Container con = new V1Container()
              .name(instanceName.toLowerCase().replace('.', '-'))
              .image(net.getDockerRepositoryPrefix()+instance.getComponentType().toLowerCase())
              .args(MontiThingsUtil.getRunArguments(instanceName, net.getMqttHost(), net.getMqttPort()));
          
          podSpec.addContainersItem(con);
        }
        
        String labelValue = "iot-pod-"+k8sClientID; 
        
        V1ObjectMeta podMeta = new V1ObjectMeta()
            .putLabelsItem("iotdeployment", labelValue);
        
        V1PodTemplateSpec template = new V1PodTemplateSpec()
            .metadata(podMeta)
            .spec(podSpec);
        
        V1DeploymentSpec deploySpec = new V1DeploymentSpec()
          .replicas(1)
          .template(template)
          .selector(new V1LabelSelector().putMatchLabelsItem("iotdeployment", labelValue));
        
        V1Deployment deployment = new V1Deployment()
            .spec(deploySpec)
            .metadata(new V1ObjectMeta().name(labelValue));
        
        try {
          apiApps.deleteNamespacedDeployment(labelValue, "default", null, null, null, null, null, null);
        } catch(ApiException ex) {
          // This may fail. Checking whether a deployment already exists with
          // this name and then deleting it would be more expensive.
        }
        
        try {
          apiApps.createNamespacedDeployment("default", deployment, null, null, null);
        }
        catch (ApiException e1) {
          System.out.println(e1.getResponseBody());
          e1.printStackTrace();
        }
      }
    }
  }
  
  @Override
  public Collection<DeployClient> getClients() {
    return Collections.unmodifiableCollection(this.clients.values());
  }
  
  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }
  
  //////////// Methods for ResourceEventHandler<V1Node> interface. ////////////
  
  @Override
  public void onUpdate(V1Node oldObj, V1Node newObj) {
    K8sDeployClient client = clients.get(oldObj);
    if (client != null) {
      updateClientWithNode(client, newObj);
      clients.remove(oldObj);
      clients.put(newObj, client);
    }
  }
  
  @Override
  public void onDelete(V1Node obj, boolean deletedFinalStateUnknown) {
    K8sDeployClient client = clients.get(obj);
    if (client != null) {
      client.setOnline(false);
      listener.onClientOffline(client);
    }
  }
  
  @Override
  public void onAdd(V1Node obj) {
    K8sDeployClient client = createClientForNode(obj);
    if(client != null) {
      clients.put(obj, client);      
    }
  }
  
}

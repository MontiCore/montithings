// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.k8s;

import io.kubernetes.client.informer.ResourceEventHandler;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.CallGeneratorParams;
import io.kubernetes.client.util.Config;
import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;
import montithings.services.iot_manager.server.util.MontiThingsUtil;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class K8sDeployTargetProvider implements IDeployTargetProvider, ResourceEventHandler<V1Node> {
  
  private static final String LITERAL_NODE_READY = "Ready";
  private static final String PREFIX_CLIENTID = "k8s-";
  private static final String K8S_NAMESPACE = "default";
  
  private final long providerID;
  
  private final ApiClient client;
  private final CoreV1Api apiCore;
  private final AppsV1Api apiApps;
  
  private SharedInformerFactory informerFactory;
  
  private final Map<V1Node, K8sDeployClient> clients = new HashMap<>();
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  public K8sDeployTargetProvider(long providerID, String hostURL, String token) throws IOException {
    this(providerID, Config.fromToken(hostURL, token, false));
  }
  
  public K8sDeployTargetProvider(long providerID, ApiClient client) throws IOException {
    this.client = client;
    this.providerID = providerID;
    this.apiCore = new CoreV1Api(client);
    this.apiApps = new AppsV1Api(client);
  }
  
  private void startListening() {
    informerFactory = new SharedInformerFactory(this.client);
    SharedIndexInformer<V1Node> nodeInformer = informerFactory.sharedIndexInformerFor((CallGeneratorParams params) -> {
      return this.apiCore.listNodeCall(null, null, null, null, null, null, params.resourceVersion, null, params.timeoutSeconds, params.watch, null);
    }, V1Node.class, V1NodeList.class);
    nodeInformer.addEventHandler(this);
    informerFactory.startAllRegisteredInformers();
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
      dc.setTargetProviderID(this.providerID);
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
      
      
      // Load hardware. In k8s, we store it as as labels whose keys are prefixed
      // with "hardware". The value of the label represents the hardware. 
      List<String> hardware = new LinkedList<String>();
      for(Entry<String, String> label : node.getMetadata().getLabels().entrySet()) {
        if(label.getKey().startsWith("hardware")) {
          String hw = label.getValue();
          hardware.add(hw);
        }
      }
      dc.setHardware(hardware.toArray(new String[hardware.size()]));
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
        
        String labelValue = "iot-pod-"+k8sClientID;
        
        if(instances.length == 0) {
          // remove deployment if no instance is scheduled
          try {
            apiApps.deleteNamespacedDeployment(labelValue, K8S_NAMESPACE, null, null, null, null, null, null);
          }
          catch (ApiException e1) {
            e1.printStackTrace();
          }
        } else {
          // otherwise deploy instances
          
          // Add a container for each application instance.
          for(String instanceName : instances) {
            InstanceInfo instance = deploymentInfo.getInstanceInfo(instanceName);
            if(instance == null) {
              throw new RuntimeException("Found invalid instance name in distribution!");
            }
            
            V1Container con = new V1Container()
                .name(instanceName.toLowerCase().replace('.', '-'))
                .image(net.getDockerRepositoryPrefix()+instance.getComponentType().toLowerCase())
                .args(MontiThingsUtil.getRunArguments(instanceName, net.getMqttHost(), net.getMqttPort()));
            
            podSpec.addContainersItem(con);
          }
          
          
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
            apiApps.deleteNamespacedDeployment(labelValue, K8S_NAMESPACE, null, null, null, null, null, null);
          } catch(ApiException ignored) {
            // This may fail. Checking whether a deployment already exists with
            // this name and then deleting it would be more expensive.
          }
          
          try {
            apiApps.createNamespacedDeployment(K8S_NAMESPACE, deployment, null, null, null);
          }
          catch (ApiException e1) {
            System.err.println(e1.getResponseBody());
            e1.printStackTrace();
          }
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

  @Override
  public void initialize() throws DeploymentException {
    this.startListening();
  }

  @Override
  public void close() throws DeploymentException {
    if(this.informerFactory != null) {
      this.informerFactory.stopAllRegisteredInformers();      
    }
  }
  
}

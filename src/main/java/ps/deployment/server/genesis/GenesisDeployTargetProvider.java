package ps.deployment.server.genesis;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

public class GenesisDeployTargetProvider implements IDeployTargetProvider {
  
  public static void main(String[] args) {
    try {
      new GenesisDeployTargetProvider(0, new URL("http://127.0.0.1:8000")).initialize();
    }
    catch (DeploymentException | MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  private final OkHttpClient httpClient;
  private final URL endpointURL;
  
  private final long providerID;
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  private List<DeployClient> clients = new ArrayList<DeployClient>();
  private GenesisDeployModel model = null;
  private boolean running = true;
  
  public GenesisDeployTargetProvider(long providerID, URL endpointURL) {
    this.providerID = providerID;
    this.httpClient = new OkHttpClient.Builder().build();
    this.endpointURL = endpointURL;
  }
  
  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException {
    // index instance info for efficient access
    HashMap<String, InstanceInfo> instanceInfos = new HashMap<>();
    for (InstanceInfo info : deploymentInfo.getInstances()) {
      instanceInfos.put(info.getInstanceName(), info);
    }
    
    if (this.model == null) {
      // This may never be the case for a properly set up provider
      throw new DeploymentException("GeneSIS provider (" + this.providerID + ") is not properly set up.");
    }
    
    // copy current model & clean it
    GenesisDeployModel model = new GenesisDeployModel(this.model.getModel().deepCopy());
    model.clearContainments();
    model.clearSoftwareComponents();
    
    // add new software components & assignments to deploy clients
    // (containments)
    for (Entry<String, String[]> e : distribution.getDistributionMap().entrySet()) {
      String clientID = e.getKey();
      if (this.hasClientWithID(clientID)) {
        String[] instances = e.getValue();
        for (String instanceName : instances) {
          InstanceInfo info = instanceInfos.get(instanceName);
          String dockerImage = net.getDockerRepositoryPrefix() + info.getComponentType().toLowerCase();
          String args = MontiThingsUtil.getRunArgumentsAsString(instanceName, net.getMqttHost(), net.getMqttPort());
          model.addMontiThingsDeployment(dockerImage, args, clientID);
        }
      }
    }
    
    // send update to GeneSIS server
    try {
      String modelStr = model.getModel().toString();
      // modelStr += "wserdbgu832m8";
      Request req = new Request.Builder().url(new URL(endpointURL, "/genesis/deploy")).post(RequestBody.create(modelStr.getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json; charset=utf-8"))).build();
      Response resp = httpClient.newCall(req).execute();
      if (resp.code() != 200) {
        throw new DeploymentException("GeneSIS failed to accept deployment");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    // update local model
    this.model = model;
  }
  
  @Override
  public Collection<DeployClient> getClients() {
    return this.clients;
  }
  
  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }
  
  private boolean hasClientWithID(String clientID) {
    return clients.stream().anyMatch((c) -> c.getClientID().equals(clientID));
  }
  
  private void refreshModel() {
    try {
      // fetch current model from GeneSIS server & prepare it
      Request req = new Request.Builder().get().url(new URL(endpointURL, "/genesis/model")).build();
      Response resp = httpClient.newCall(req).execute();
      String strModel = resp.body().string();
      JsonObject jModel = JsonParser.parseString(strModel).getAsJsonObject();
      this.model = new GenesisDeployModel(jModel);
    }
    catch (IOException e) {
      System.err.println("Could not connect to GeneSIS Engine. (Provider #" + this.providerID + ")");
    }
  }
  
  private void refreshClients() throws DeploymentException {
    // Fail quickly: Nothing to do.
    if (this.model == null)
      return;
    
    // find added clients
    List<String> addedHostPorts = new LinkedList<>();
    
    List<String> modelHostPorts = this.model.getHostPortsSSH();
    for (String hostPort : modelHostPorts) {
      if (!this.hasClientWithID(hostPort)) {
        // no client with this id is registered yet
        addedHostPorts.add(hostPort);
      }
    }
    
    // remove absent clients
    for (DeployClient client : this.clients) {
      if (!modelHostPorts.contains(client.getClientID())) {
        // client is no longer present in GeneSIS model
        client.setOnline(false);
        this.listener.onClientOffline(client);
      }
    }
    
    // add added clients
    for (String hostPortId : addedHostPorts) {
      DeployClient client = createClientForHostPort(hostPortId);
      if (client != null) {
        this.clients.add(client);
        this.listener.onClientOnline(client);
      }
    }
  }
  
  private DeployClient createClientForHostPort(String hostPort) {
    String building = null;
    String floor = null;
    String room = null;
    
    // Get device_type of GeneSIS host.
    // IF this host is properly setup, its device_type is a JSON-formatted
    // string containing information about its location and hardware.
    List<String> hardware = new LinkedList<String>();
    String deviceType = model.getDeviceTypeOfHost(model.extractHost(hostPort));
    try {
      JsonObject jDeviceType = JsonParser.parseString(deviceType).getAsJsonObject();
      building = jDeviceType.get("building").getAsString();
      floor = jDeviceType.get("floor").getAsString();
      room = jDeviceType.get("room").getAsString();
      
      JsonElement jHardware = jDeviceType.get("hardware");
      if (jHardware != null && jHardware.isJsonArray()) {
        for (JsonElement jhw : jHardware.getAsJsonArray()) {
          hardware.add(jhw.getAsString());
        }
      }
    }
    catch (JsonParseException | IllegalStateException | NullPointerException e) {
      // This client is not properly set up. Thus we'll refuse to use it.
      return null;
    }
    
    if (building == null || floor == null || room == null) {
      // This client is not properly set up. Thus we'll refuse to use it.
      return null;
    }
    
    LocationSpecifier loc = LocationSpecifier.create(building, floor, room);
    DeployClient client = DeployClient.create(hostPort, true, loc, providerID, hardware.toArray(new String[hardware.size()]));
    
    return client;
  }
  
  private void startPolling() {
    while (this.running) {
      try {
        this.refreshModel();
        this.refreshClients();
        System.out.println(this.clients);
        Thread.sleep(30_000);
      }
      catch (InterruptedException | DeploymentException e) {
      }
    }
  }
  
  @Override
  public void initialize() throws DeploymentException {
    refreshModel();
    refreshClients();
    
    // start polling for periodic updates
    Thread updaterThread = new Thread(this::startPolling);
    updaterThread.setDaemon(true);
    updaterThread.start();
  }
  
  @Override
  public void close() throws DeploymentException {
    this.running = false;
  }
  
}

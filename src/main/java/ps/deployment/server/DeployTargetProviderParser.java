package ps.deployment.server;

import java.util.HashMap;
import org.eclipse.paho.client.mqttv3.MqttClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ps.deployment.server.exception.DeploymentException;
import ps.deployment.server.k8s.K8sDeployTargetProvider;
import ps.deployment.server.util.ThrowingFunction;

public class DeployTargetProviderParser {
  
  public static IDeployTargetProvider parse(JsonElement json) throws DeploymentException {
    try {
      if (json.isJsonArray()) {
        // Pool provider
        return parsePoolProvider(json.getAsJsonArray());
      }
      else {
        // otherwise it is a typed provider
        
        // register types
        HashMap<String, ThrowingFunction<JsonObject, IDeployTargetProvider, DeploymentException>> constructors = new HashMap<>();
        constructors.put("BASIC", DeployTargetProviderParser::parseBasicProvider);
        constructors.put("KUBERNETES", DeployTargetProviderParser::parseKubernetesProvider);
        
        JsonObject jo = json.getAsJsonObject();
        String type = jo.get("type").getAsString();
        
        ThrowingFunction<JsonObject, IDeployTargetProvider, DeploymentException> constructor = constructors.get(type);
        if (constructor == null) {
          throw new DeploymentException("Unknown deployment target type: " + type);
        }
        
        return constructor.apply(jo);
      }
    }
    catch (Exception e) {
      throw new DeploymentException("Could not parse deployment provider from json.", e);
    }
  }
  
  public static PoolDeployTargetProvider parsePoolProvider(JsonArray jarr) throws DeploymentException {
    PoolDeployTargetProvider pool = new PoolDeployTargetProvider();
    // parse nested providers and add them to pool
    for (JsonElement je : jarr) {
      pool.addProvider(parse(je));
    }
    return pool;
  }
  
  public static DefaultDeployTargetProvider parseBasicProvider(JsonObject json) throws DeploymentException {
    try {
      String mqttAddress = json.get("mqttAddress").getAsString();
      String mqttURI = "tcp://" + mqttAddress;
      if (mqttURI.contains(":")) {
        // if no port is given, add default port
        mqttURI += ":1883";
      }
      long providerID = json.get("id").getAsLong();
      
      MqttClient client = new MqttClient(mqttURI, MqttClient.generateClientId());
      return new DefaultDeployTargetProvider(providerID, client);
    }
    catch (Exception e) {
      throw new DeploymentException(e);
    }
  }
  
  public static K8sDeployTargetProvider parseKubernetesProvider(JsonObject json) throws DeploymentException {
    try {
      String endpointURL = json.get("endpoint").getAsString();
      String token = json.get("token").getAsString();
      token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImpvRXFtY2Q3ZHJ3Q001OElWNXI1ME1vRElrUmw5eElFd0dCMk83a3VFMkkifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImlvdC10b2tlbi1jOXJyaCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJpb3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI1ZmY3Zjc5Zi0xNjQxLTRiYTctOGZjNC03MDIzMDVkNWEzZjEiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDppb3QifQ.STCH2LLFlyhTXd5mA1IppKW2-mYUBpwNdQm3QxEP3qgMKJNK0vBJqgD26861CqFo8UwHAom4ZBj3a_jnvazVakYLWUz-qR-9Wzvco8li3yNfZWRCZR5QAUUX2drnajtLIf8-CErw282Y4UPZrGrSWKBJfYOG_CMW_ZVwDE1aAQFw9nfDFj2TZb7CyL5WccoUVKptqsJYkUqLjcV0d3rzuFyquXMdCWVp5tvyys8KU1f3he8uuLYYXGJhqZ3OziVmULX0SA1dWU7VJ9sFjJknsyfl_Q8X0HQ6Lb9j_QkUOPh_PntrVR56Oyw6-C5KBQRx3p-wVyvjLfeUUuQQgtlwfw";
      long providerID = json.get("id").getAsLong();
      
      return new K8sDeployTargetProvider(providerID, endpointURL, token);
    }
    catch (Exception e) {
      throw new DeploymentException(e);
    }
  }
  
}

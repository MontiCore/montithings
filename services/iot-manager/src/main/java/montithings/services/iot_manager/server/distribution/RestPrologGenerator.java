// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.Utils;
import montithings.services.iot_manager.server.data.DeployClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RestPrologGenerator implements IPrologGenerator {
  
  public RestPrologGenerator() {
    
  }
  
  @Override
  public CompletableFuture<String> generateFacts(Collection<DeployClient> clients) {
    return CompletableFuture.supplyAsync(() -> generateFactsJson(clients).toString()).thenApply(this::requestPrologFacts);
  }
  
  private JsonObject generateFactsJson(Collection<DeployClient> clients) {
    JsonObject jsonBase = new JsonObject();
    JsonObject jsonDevices = new JsonObject();
    Gson gson = new GsonBuilder().create();
    
    for (DeployClient client : clients) {
      JsonObject jsonClient = new JsonObject();
      jsonClient.addProperty("type", "device");
      jsonClient.addProperty("state", client.isOnline() ? "online" : "offline");
      
      JsonArray jsonHardware = gson.toJsonTree(client.getHardware()).getAsJsonArray();
      jsonClient.add("hardware", jsonHardware);
      
      JsonObject jsonLocation = gson.toJsonTree(client.getLocation()).getAsJsonObject();
      jsonClient.add("location", jsonLocation);
      
      jsonDevices.add(client.getClientID(), jsonClient);
    }
    
    jsonBase.add("devices", jsonDevices);
    return jsonBase;
  }
  
  private String sendPost(String endpoint, String content) {
    try {
      URL url = new URL("http://prolog:5004/" + endpoint);
      byte[] payload = content.getBytes(StandardCharsets.UTF_8);
      
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Length", String.valueOf(payload.length));
      con.setDoOutput(true);
      con.getOutputStream().write(payload);
      
      return new String(Utils.readAllBytes(con.getInputStream()), StandardCharsets.UTF_8);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private String requestPrologFacts(String strFactsJson) {
    return sendPost("facts", strFactsJson);
  }
  
  @Override
  public CompletableFuture<String> generateQuery(String jsonConfig) {
    return CompletableFuture.supplyAsync(() -> jsonConfig).thenApply(this::requestPrologQuery);
  }
  
  private String requestPrologQuery(String strConfigJson) {
    return sendPost("config", strConfigJson);
  }
  
}

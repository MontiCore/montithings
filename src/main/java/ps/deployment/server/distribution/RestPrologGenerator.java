package ps.deployment.server.distribution;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ps.deployment.server.data.DeployClient;

public class RestPrologGenerator implements IPrologGenerator {
  
  private HttpClient httpClient;
  
  public RestPrologGenerator() {
    this.httpClient = HttpClient.newHttpClient();
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
      HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:5004/" + endpoint)).POST(HttpRequest.BodyPublishers.ofString(content)).build();
      HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
      return resp.body();
    }
    catch (IOException | InterruptedException | URISyntaxException e) {
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

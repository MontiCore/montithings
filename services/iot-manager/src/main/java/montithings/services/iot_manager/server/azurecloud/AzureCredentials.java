package montithings.services.iot_manager.server.azurecloud;

public class AzureCredentials {
  private String clientId, clientSecret, subscriptionId, tenantId;

  public AzureCredentials(String clientId, String clientSecret, String subscriptionId, String tenantId) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.subscriptionId = subscriptionId;
    this.tenantId = tenantId;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}

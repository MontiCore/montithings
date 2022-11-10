package montithings.services.iot_manager.server.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.TerraformInfo;

public class ApplyTerraformDTO {
  private TerraformInfo[] files;
  private AzureCredentials credentials;

  public ApplyTerraformDTO(AzureCredentials credentials, TerraformInfo[] files) {
    this.files = files;
    this.credentials = credentials;
  }

  public TerraformInfo[] getFiles() {
    return this.files;
  }

  public AzureCredentials getCredentials() {
    return credentials;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }

}

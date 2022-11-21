package montithings.services.iot_manager.server.dto;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.TerraformInfo;

public class ApplyTerraformDTO {
  private List<TerraformInfo> files;
  private AzureCredentials credentials;

  public ApplyTerraformDTO(AzureCredentials credentials, List<TerraformInfo> files) {
    this.files = files;
    this.credentials = credentials;
  }

  public List<TerraformInfo> getFiles() {
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

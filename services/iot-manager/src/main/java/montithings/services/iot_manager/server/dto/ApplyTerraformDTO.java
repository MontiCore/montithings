package montithings.services.iot_manager.server.dto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.TerraformInfo;

public class ApplyTerraformDTO {
  private List<TerraformInfo> files;
  private AzureCredentials credentials;
  private String storageAccountName;
  private Optional<String> tfstate;

  public ApplyTerraformDTO(AzureCredentials credentials, List<TerraformInfo> files, String storageAccountName,
      Optional<String> tfstate) {
    this.files = files;
    this.credentials = credentials;
    this.storageAccountName = storageAccountName;
    this.tfstate = tfstate;
  }

  public List<TerraformInfo> getFiles() {
    return this.files;
  }

  public AzureCredentials getCredentials() {
    return credentials;
  }

  public String getStorageAccountName() {
    return storageAccountName;
  }

  public Optional<String> getTfstate() {
    return tfstate;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }

}

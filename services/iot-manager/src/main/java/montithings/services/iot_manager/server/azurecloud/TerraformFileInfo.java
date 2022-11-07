package montithings.services.iot_manager.server.azurecloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TerraformFileInfo {
  private String filename;
  private String filecontent;

  public TerraformFileInfo(String filename, String filecontent) {
    this.filename = filename;
    this.filecontent = filecontent;
  }

  public String getFilename() {
    return this.filename;
  }

  public String getFilecontent() {
    return this.filecontent;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }
}

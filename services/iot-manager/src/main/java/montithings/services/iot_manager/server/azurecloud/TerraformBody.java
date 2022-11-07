package montithings.services.iot_manager.server.azurecloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TerraformBody {
  private TerraformFileInfo[] files;

  public TerraformBody(TerraformFileInfo... files) {
    this.files = files;
  }

  public TerraformFileInfo[] getFilea() {
    return this.files;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }
}

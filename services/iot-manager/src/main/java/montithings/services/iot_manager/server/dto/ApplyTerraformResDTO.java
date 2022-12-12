package montithings.services.iot_manager.server.dto;

import java.util.Map;

public class ApplyTerraformResDTO {
  Map<String, String> envvars;
  String tfstate;

  // Default constructor required for Jackson desirialization
  public ApplyTerraformResDTO() {
    super();
  }

  public ApplyTerraformResDTO(Map<String, String> envvars, String tfstate) {
    this.envvars = envvars;
    this.tfstate = tfstate;
  }

  public Map<String, String> getEnvvars() {
    return envvars;
  }

  public String getTfstate() {
    return tfstate;
  }

  public void setEnvvars(Map<String, String> envvars) {
    this.envvars = envvars;
  }

  public void setTfstate(String tfstate) {
    this.tfstate = tfstate;
  }
}

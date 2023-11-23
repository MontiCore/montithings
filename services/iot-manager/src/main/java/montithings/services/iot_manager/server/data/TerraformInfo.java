// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TerraformInfo {
  private String filename, filecontent;

  public TerraformInfo(String filename, String filecontent) {
    this.filename = filename;
    this.filecontent = filecontent;
  }

  public String getFilename() {
    return filename;
  }

  public String getFilecontent() {
    return filecontent;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void setFilecontent(String filecontent) {
    this.filecontent = filecontent;
  }

  public String toJson() throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TerraformInfo)) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    TerraformInfo rhs = (TerraformInfo) obj;
    return new EqualsBuilder().append(filename, rhs.filename).append(filecontent, rhs.filecontent).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(filename).append(filecontent).toHashCode();
  }
}

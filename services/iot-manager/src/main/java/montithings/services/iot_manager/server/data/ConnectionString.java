package montithings.services.iot_manager.server.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConnectionString {
  byte[] rawMsgPayload;
  String connectionString;
  String instanceName;

  public ConnectionString(byte[] rawMsgPayload, String connectionString, String instanceName) {
    this.rawMsgPayload = rawMsgPayload;
    this.connectionString = connectionString;
    this.instanceName = instanceName;
  }

  public byte[] getRawMsgPayload() {
    return rawMsgPayload;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public byte[] getConnectionStringAsByte() {
    return connectionString.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConnectionString)) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    ConnectionString rhs = (ConnectionString) obj;
    return Arrays.equals(rawMsgPayload, rhs.rawMsgPayload);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(rawMsgPayload).toHashCode();
  }
}

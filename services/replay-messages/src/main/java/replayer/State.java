// (c) https://github.com/MontiCore/monticore
package replayer;

import java.util.HashMap;
import java.util.Map;

public class State {

  String state;

  Map<String, Integer> messagesToSkipForPort = new HashMap<>();

  public void skipMessages(String port, int messagesToSkip) {
    messagesToSkipForPort.put(port, messagesToSkip);
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public State(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }

  public Map<String, Integer> getMessagesToSkipForPort() {
    return messagesToSkipForPort;
  }
}

// (c) https://github.com/MontiCore/monticore
package replayer;

public class ReplayRequest {
  protected String topicToReplay;
  protected String targetTopic;

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public ReplayRequest(String topicToReplay, String targetTopic) {
    this.topicToReplay = topicToReplay;
    this.targetTopic = targetTopic;
  }

  public String getTopicToReplay() {
    return topicToReplay;
  }

  public void setTopicToReplay(String topicToReplay) {
    this.topicToReplay = topicToReplay;
  }

  public String getTargetTopic() {
    return targetTopic;
  }

  public void setTargetTopic(String targetTopic) {
    this.targetTopic = targetTopic;
  }
}

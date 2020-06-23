package montithings.generator.codegen;

/**
 * Bundle of parameters for montithings2cpp generator.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class ConfigParams {
  /** property for target platform */
  private TargetPlatform targetPlatform = TargetPlatform.GENERIC;

  public TargetPlatform getTargetPlatform() {
    return targetPlatform;
  }

  public void setTargetPlatform(TargetPlatform targetPlatform) {
    this.targetPlatform = targetPlatform;
  }

  public enum TargetPlatform {
    GENERIC,
    DSA_VCG,
    ARDUINO
  }
}

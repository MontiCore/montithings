// (c) https://github.com/MontiCore/monticore
package mtconfig.util;

/**
 * The enum of all MTConfig errors. Extends the mixing interface {@link mtconfig.util.Error}
 */
public enum MTConfigError implements mtconfig.util.Error {
  MISSING_REQUIREMENT_NAME("0xMTCFG1030", "ASTRequirementStatement '%s' at <%d,%d> has no corresponding ComponentTypeSymbol."),
  MISSING_COMPONENT_NAME("0xMTCFG1031", "ASTCompConfig '%s' at <%d,%d> has no corresponding ComponentTypeSymbol."),
  MISSING_PORT_NAME("0xMTCFG1032", "ASTPortTemplateTag '%s' at <%d,%d> has no corresponding PortSymbol."),
  MULTIPLE_COMPONENTS("0xMTCFG1033", "Config references multiple component types: %s"),
  FILENAME_MATCHES_CONFIG("0xMTCFG1034", "Config '%s' does not match its filename"),
  PACKAGENAME_MATCHES_CONFIG("0xMTCFG1035", "Package '%s' does not match its relative filepath '%s'"),
  HOOKPOINT_EXISTS("0xMTCFG1036", "Hookpoint '%s' does not exist (use one of these: %s)"),
  MQTT_NO_ARGS("0xMTCFG1037", "MQTT Hookpoint '%s' must not take arguements."),
  ONLY_ONE_EVERY("0xMTCFG1038", "Port '%s' has multiple 'every' statements. At most one is allowed.")
;

  private final String errorCode;
  private final String errorMessage;

  MTConfigError(String errorCode, String errorMessage) {
    assert (errorCode != null);
    assert (errorMessage != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * @return The unique error code of this error.
   */
  @Override
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String printErrorMessage() {
    return this.errorMessage;
  }

  @Override
  public String toString() {
    return this.getErrorCode() + ": " + this.printErrorMessage();
  }
}
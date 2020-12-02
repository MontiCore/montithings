// (c) https://github.com/MontiCore/monticore
package mtconfig.util;

/**
 * The enum of all MTConfig errors. Extends the mixing interface {@link mtconfig.util.Error}
 */
public enum MTConfigError implements mtconfig.util.Error {
  MISSING_REQUIREMENT_NAME("0xPHY1030", "ASTRequirementStatement '%s' at <%d,%d> has no corresponding ComponentTypeSymbols."),
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
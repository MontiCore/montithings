/* (c) https://github.com/MontiCore/monticore */
package bindings.util;

/**
 * The enum of all Bindings errors. Extends the mixing interface {@link Error}
 */
public enum BindingsError implements bindings.util.Error {
  NO_MODEL_IMPLEMENTATION("0xMTB0000", "Implementation has no model implementation!"),
  NOT_SAME_PORTS_IMPLEMENTED("0xMTB0001", "Implementation and Interface don't implement the same ports!"),
  NO_MODEL_INTERFACE("0xMTB0002", "Interface has no model Interface!"),
  LEFT_SIDE_NO_INTERFACE("0xMTB0003", "Left side is no interface!"),
  RIGHT_SIDE_NO_IMPLEMENTATION("0xMTB0004", "Right side is no implementation!");

  private final String errorCode;
  private final String errorMessage;

  BindingsError(String errorCode, String errorMessage) {
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
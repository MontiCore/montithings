// (c) https://github.com/MontiCore/monticore
package montithings.util;

/**
 * The enum of all MontiThings errors. Extends the mixing interface {@link Error}
 */
public enum MontiThingsError implements montithings.util.Error {
  NO_ENCLOSING_SCOPE("0xMT1400", "No Component enclosing Scope. Was SymbolTable initialized?"),
  NOT_INTERFACE("0xMT1410", "Generic %s for component instance %s in component %s line %s does not extend only interface models."
      + " Is an resolveable interface component model available?"),
  TYPE_NOT_FOUND("0xMT1420", "Type %s line %s could not be found."),
  IMPLEMENTATION_MISSING("0xMT1430", "Implementation Component %s of SubComponent %s in component %s line %s does not exist."
      + "Is an resolveable implementing component model available?"),
  INTERFACE_IMPLEMENTS_INTERFACE("0xMT1440", "Implementation Component %s of SubComponent %s in component %s can not be an interface component line %s."),
  NOT_FITS_INTERFACE("0xMT1450", "Implementation Component %s of SubComponent %s in component %s line %s does not meet required interface component specification."),
  GENERIC_PARAMTER_INTERFACE_NOT_FOUND("0xMT1460", "Interface component %s of Generic %s in component %s line %s not found."
      + " Is the interface component model available and resolve able?"),
  GENERIC_PARAMTER_NOT_FITS_INTERFACE("0xMT1470", "Generic %s of SubComponent %s in component %s does not allow the interface component %s line %s."
      + "Is a valid resolve able interface component model available and does the generic extend it?"),
  GENERIC_PARAMETER_NEEDS_INTERFACE("0xMT1480", "Generic %s of SubComponent %s in component %s requires an interface component line %s."
      + "Does the generic extend an component?");

  private final String errorCode;
  private final String errorMessage;

  MontiThingsError(String errorCode, String errorMessage) {
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
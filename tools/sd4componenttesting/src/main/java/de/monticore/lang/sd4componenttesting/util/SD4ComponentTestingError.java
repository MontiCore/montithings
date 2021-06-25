// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.util;

/**
 * The enum of all SD4ComponentTesting errors. Extends the mixing interface {@link Error}
 */
public enum SD4ComponentTestingError implements Error {
  NO_MAIN_COMPONENT_IMPLEMENTATION("0xSD4CPT1010", "Main Component Instance '%s' has no model file!"),
  NO_MODEL_IMPLEMENTATION("0xSD4CPT1020", "Implementation '%s' has no model file!"),
  NOT_SAME_PORTS_IMPLEMENTED("0xSD4CPT1030", "Interface '%s' and Implementation '%s' don't implement the same ports!");

  protected final String errorCode;
  protected final String errorMessage;

  SD4ComponentTestingError(String errorCode, String errorMessage) {
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

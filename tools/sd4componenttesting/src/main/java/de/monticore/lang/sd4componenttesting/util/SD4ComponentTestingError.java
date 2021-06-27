// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.util;

/**
 * The enum of all SD4ComponentTesting errors. Extends the mixing interface {@link Error}
 */
public enum SD4ComponentTestingError implements Error {
  NO_MAIN_COMPONENT_IMPLEMENTATION("0xSD4CPT1010", "Main Component Instance '%s' has no model file!"),
  UNKNOWN_COMPONENT_INSTANCE_IN_PORT_ACCESS("0xSD4CPT1030", "ComponentInstance '%s' of PortAccess '%s' could not be found in MainComponent '%s'!"),
  UNKNOWN_PORT_ACCESS("0xSD4CPT1040", "The Port Access '%s' could not be found!"),

  CONNECTION_NOT_VALID("0xSD4CPT1050", "Connection '%s' is not valid"),
  CONNECTION_NOT_VALID_WRONG_VALUE_AMOUNT("0xSD4CPT1060", "Connection '%s' is not valid (wrong value amount)"),
  MAIN_OUTPUT_COMPONENT_GIVEN("0xSD4CPT1070", "Component '%s' given in Output Connection '%s'"),
  MAIN_OUTPUT_UNKNOWN_PORT("0xSD4CPT1080", "Output Port '%s' of Connection '%s' could not be found in MainComponent '%s'"),
  MAIN_INPUT_COMPONENT_GIVEN("0xSD4CPT1090", "Component '%s' given in Input Connection '%s'"),
  MAIN_INPUT_UNKNOWN_PORT("0xSD4CPT1100", "Input Port '%s' of Connection '%s' could not be found in MainComponent '%s'"),

  CONNECTION_SOURCE_UNKNOWN_PORT("0xSD4CPT1110", "Output Port '%s' of Connection '%s' could not be found in Component '%s'"),
  CONNECTION_TARGET_UNKNOWN_PORT("0xSD4CPT1120", "Input Port '%s' of Connection '%s' could not be found in Component '%s'"),

  CONNECTION_NOT_DEFINED_AS_CONNECTOR("0xSD4CPT1130", "Connection '%s' is not defined in MainComponent '%s' as connector");

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

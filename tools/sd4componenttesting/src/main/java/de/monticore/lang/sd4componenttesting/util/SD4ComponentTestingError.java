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

  CONNECTION_NOT_DEFINED_AS_CONNECTOR("0xSD4CPT1130", "Connection '%s' is not defined in MainComponent '%s' as connector"),

  EXPRESSION_SET_AND_EXPRESSION("0xSD4CPT1820", "SetAndExpression is not supported."),
  EXPRESSION_SET_OR_EXPRESSION("0xSD4CPT1821", "SetOrExpression is not supported."),
  EXPRESSION_SET_UNION_EXPRESSION("0xSD4CPT1822", "SetUnionExpression is not supported."),
  EXPRESSION_LEFT_SET_COMPREHENSIONS("0xSD4CPT1824", "Expressions at the left side of SetComprehensions are not supported"),
  EXPRESSION_RIGHT_SET_COMPREHENSIONS("0xSD4CPT1825", "Only expressions are supported at the right side of set comprehensions"),
  EXPRESSION_FOR_ALL_EXPRESSION("0xSD4CPT1826", "Only one InDeclaration is supported for every ForallExpression"),
  EXPRESSION_EXISTS_EXPRESSION("0xSD4CPT1827", "Only one InDeclaration is supported for every ExistsExpression"),
  EXPRESSION_ANY_EXPRESSION("0xSD4CPT1828", "Only SetEnumerations or SetComprehensions are allowed in AnyExpressions"),
  EXPRESSION_OCL_AT_PRE_QUALIFICATION("0xSD4CPT1829", "OCLAtPreQualification can only be applied to variables of components"),
  EXPRESSION_OCL_ARRAY_QUALIFICATION("0xSD4CPT1830", "OCLArrayQualification is not supported"),
  EXPRESSION_OCL_TRANSITIVE_QUALIFICATION("0xSD4CPT1831", "OCLTransitiveQualification is not supported"),
  EXPRESSION_IN_DECLARATION_WITHOUT_EXPRESSIONS("0xSD4CPT1832", "InDeclarations without Expressions in '%s' are not supported"),
  EXPRESSION_IN_DECLARATION("0xSD4CPT1833", "Only SetEnumerations and SetComprehensions are supported as Expressions in InDeclarations of '%s'"),
  EXPRESSION_IN_DECLARATION_SET_DEFINITIONS("0xSD4CPT1834", "only SetValueItems and SetValueRanges are supported in InDeclaration SetDefinitions"),
  EXPRESSION_SET_COMPREHENSIONS("0xSD4CPT1835", "SetComprehensions in InDeclarations are only supported if the left side is a generator declaration"),
  EXPRESSION_GENERATOR_DECLARATIONS("0xSD4CPT1836", "Set building expressions other than SetEnumerations are not supported in GeneratorDeclarations of SetComprehensions"),

  EXPRESSION_FIELD_ACCESS_EXPRESSION_NO_COMPONENT_NAME("0xSD4CPT1837", "FieldAccessExpressions are only allowed to access Ports of Components"),
  EXPRESSION_FIELD_ACCESS_EXPRESSION_NO_COMPONENT_FOUND("0xSD4CPT1838", "'%s' not found as FieldAccessExpression"),

  ASSIGNMENT_EXPRESSIONS("0xSD4CPT1839", "SD4Component testing currently not supporting assignment expressions"),

  IDENTIFIER_UNKNOWN("0xSD4CPT1510", "The identifier '%s' cannot be resolved.");
  
  DELAY_GREATER_THAN_ZERO("0xSD4CPT1140", "Delay '%s' must be greater than zero."),
  DELAY_UNIT_UNKNOWN("0xSD4CPT1150", "The unit '%s' is unknown.");

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

// (c) https://github.com/MontiCore/monticore
package montithings.util;

/**
 * The enum of all MontiThings errors. Extends the mixing interface {@link Error}
 */
public enum MontiThingsError implements montithings.util.Error {
  NO_ENCLOSING_SCOPE("0xMT1400", "No Component enclosing Scope. Was SymbolTable initialized?"),
  NOT_INTERFACE("0xMT1410",
    "Generic '%s' for component instance '%s' in component '%s' line '%s' does not extend only interface models."
      + " Is an resolveable interface component model available?"),
  TYPE_NOT_FOUND("0xMT1420", "Type '%s' line '%s' could not be found."),
  SAME_INTERFACE_MULTIPLE_TIMES("0xMT1425",
    "Component '%s' implements the same interface more than once."),
  IMPLEMENTATION_MISSING("0xMT1430",
    "Implementation Component '%s' of SubComponent '%s' in component '%s' line '%s' does not exist."
      + "Is an resolveable implementing component model available?"),
  INTERFACE_IMPLEMENTS_INTERFACE("0xMT1440",
    "Implementation Component '%s' of SubComponent '%s' in component '%s' can not be an interface component line '%s'."),
  NOT_FITS_INTERFACE("0xMT1450",
    "Implementation Component '%s' of SubComponent '%s' in component '%s' line '%s' does not meet required interface component specification."),
  GENERIC_PARAMTER_INTERFACE_NOT_FOUND("0xMT1460",
    "Interface component '%s' of Generic '%s' in component '%s' line '%s' not found."
      + " Is the interface component model available and resolve able?"),
  GENERIC_PARAMTER_NOT_FITS_INTERFACE("0xMT1470",
    "Generic '%s' of SubComponent '%s' in component '%s' does not allow the interface component '%s' line '%s'."
      + "Is a valid resolve able interface component model available and does the generic extend it?"),
  GENERIC_PARAMETER_NEEDS_INTERFACE("0xMT1480",
    "Generic '%s' of SubComponent '%s' in component '%s' requires an interface component line '%s'."
      + "Does the generic extend an component?"),
  NO_BEHAVIOR("0xMT1490",
    "Component '%s' has no behavior (neither in model nor hand-written code)"),
  NO_BEHAVIOR_ONLY_EVERY("0xMT1491",
    "Component '%s' has no behavior (only (an) every block(s)) but (an) incoming port(s)"),
  NO_INCOMING_PORTS_IN_EVERY_BLOCK("0xMT1495",
    "Port '%s' of component '%s' may not be referenced by an every block"),
  NO_INCOMING_PORTS_IN_EVERY_BLOCK_LOG("0xMT1496",
    "Port '%s' of component '%s' may not be referenced by an every block (including log statements)"),
  ONLY_ONE_UPDATE_INTERVAL("0xMT1500", "Update intervals should only be defined once in '%s'"),
  IDENTIFIER_UNKNOWN("0xMT1510", "The identifier '%s' cannot be resolved."),
  PUBLISH_IDENTIFIER_UNKNOWN("0xMT1511",
    "The identifier '%s' is published but does not refer to a port."),
  LOG_IDENTIFIER_UNKNOWN("0xMT1515", "Identifier '%s' is unknown. It cannot be logged."),
  UNSUPPORTED_OPERATOR("0xMT1600", "The operator '%s' is not supported."),
  POSTCONDITION_MULTIPLE_OUTPORTS("0xMT1610",
    "Postcondition '%s' references multiple outgoing ports (%s), but only one is allowed."),
  OUTPORT_WRITE_ONLY("0xMT1620", "Port '%s' is read. Outgoing ports are write-only."),
  BEHAVIOR_USES_UNDECLARED_PORT("0xMT1630",
    "Port-specific behavior for ports '%s' of component '%s' uses port '%s' without referencing it in its definition"),
  BEHAVIOR_REFERENCES_INVALID_PORT("0xMT1631",
    "Port-specific behavior for ports '%s' of component '%s' refers to port '%s' which is either an "
      + "outgoing port or does not exist in the scope of this component."),
  INCOMING_PORTS_NOT_USED("0xMT1632",
    "Incoming Ports '%s' of component '%s' are not being used by any behavior."),
  MULTIPLE_BEHAVIORS_SAME_PORTS("0xMT1633",
    "Multiple port-specific behaviors exist for the set of ports '%s' in component '%s'."),
  BEHAVIOR_PORTS_USED_ALREADY("0xMT1634",
    "Port-specific behavior for ports in component '%s' which works on the ports '%s' can never be executed, "
      +
      "as another port-specific behavior uses a subset of these ports."),

  TOOL_PARSE_IOEXCEPTION("0xMT1902", "Could not parse the file \"%s\"."),
  TOOL_FILE_WALK_IOEXCEPTION("0xMT1903",
    "Could not access the directory \"%s\" or one of its subdirectories."),

  GENERATOR_ONLY_ONE_MAIN("0xMT2001",
    "Configured both '%s' (as 'mainComponent') and '%s' (as 'main') as main components using generator parameters. Only use of them."),
  GENERATOR_MAIN_REQUIRED("0xMT2002", "You did not configure generator parameter 'mainComponent'."),
  GENERATOR_MAIN_UNKNOWN("0xMT2003",
    "Component '%s' which is set as 'mainComponent' is unknown. Possible choices are: %s"),

  GENERATOR_REPLAYDATA_REQUIRED("0xMT2004",
    "Replay mode is enabled but no data is provided. Add 'replayDataPath' option."),

  ;

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
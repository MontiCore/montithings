// (c) https://github.com/MontiCore/monticore
package cdlangextension.util;

/**
 * The enum of all CDLangExtension errors. Extends the mixing interface {@link cdlangextension.util.Error}
 */
public enum CDLangExtensionError implements cdlangextension.util.Error {
  MISSING_IMPORT_NAME("0xCDE1030", "ASTCDEImportStatement '%s' at <%d,%d> has no corresponding CDTypeSymbol."),
  EMPTY_IMPORT_FIELD("0xCDE1000", "Import Name at %s cannot be empty"),
  AMBIGUOUS_IMPORT_NAME("0xCDE1010", "Import Name '%s' at '%s' is used multiple times in Language '%s'"),
  AMBIGUOUS_LANGUAGE_NAME("0xCDE1020", "Language Name '%s' at '%s' is used multiple times");

  private final String errorCode;
  private final String errorMessage;

  CDLangExtensionError(String errorCode, String errorMessage) {
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
/* (c) https://github.com/MontiCore/monticore */
package montithings._symboltable;

import montiarc._ast.ASTValuation;
import montithings._ast.ASTAllowedValues;

/**
 * TODO
 *
 * @since 23.03.20
 */
public class PortSymbol extends montiarc._symboltable.PortSymbol {

  protected ASTValuation defaultValue = null;
  protected ASTAllowedValues allowedValues = null;

  /**
   * Constructor for a PortSymbol object.
   *
   * @param name Name of the PortSymbol
   */
  public PortSymbol(String name) {
    super(name);
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public ASTValuation getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(ASTValuation defaultValue) {
    this.defaultValue = defaultValue;
  }

  public ASTAllowedValues getAllowedValues() {
    return allowedValues;
  }

  public void setAllowedValues(ASTAllowedValues allowedValues) {
    this.allowedValues = allowedValues;
  }
}

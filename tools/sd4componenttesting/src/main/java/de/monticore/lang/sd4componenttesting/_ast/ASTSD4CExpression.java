// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._ast;

import de.monticore.lang.sd4componenttesting.util.SD4CElementType;

public class ASTSD4CExpression extends ASTSD4CExpressionTOP{
  private SD4CElementType type;

  public SD4CElementType getType() {
    return type;
  }

  public void setType(SD4CElementType type) {
    this.type = type;
  }
}

// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._ast;

import montithings.tools.sd4componenttesting.util.SD4CElementType;
import montithings.tools.sd4componenttesting.util.SD4CElementType;

public class ASTSD4CExpression extends ASTSD4CExpressionTOP{
  private SD4CElementType type;

  public SD4CElementType getType() {
    return type;
  }

  public void setType(SD4CElementType type) {
    this.type = type;
  }
}

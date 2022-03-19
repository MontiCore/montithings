// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._ast;

import montithings.tools.sd4componenttesting.util.SD4CElementType;
import montithings.tools.sd4componenttesting.util.SD4CElementType;

public interface ASTSD4CElement extends ASTSD4CElementTOP{
  public SD4CElementType getType();
  public void setType(SD4CElementType type);
}

// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._ast;

import de.monticore.lang.sd4componenttesting.util.SD4CElementType;

public interface ASTSD4CElement extends ASTSD4CElementTOP{
  public SD4CElementType getType();
  public void setType(SD4CElementType type);
}

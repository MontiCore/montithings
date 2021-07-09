package de.monticore.lang.sd4componenttesting._ast;

import de.monticore.lang.sd4componenttesting.util.SD4CElementType;

public class ASTSD4CConnection extends ASTSD4CConnectionTOP{
  private SD4CElementType type;

  public SD4CElementType getType() {
    return type;
  }

  public void setType(SD4CElementType type) {
    this.type = type;
  }
}

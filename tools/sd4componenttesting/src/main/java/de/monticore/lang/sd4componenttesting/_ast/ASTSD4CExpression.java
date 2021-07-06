package de.monticore.lang.sd4componenttesting._ast;

import de.monticore.lang.sd4componenttesting.util.ConnectionType;

public class ASTSD4CExpression extends ASTSD4CExpressionTOP{
  private ConnectionType type;

  public ConnectionType getType() {
    return type;
  }

  public void setType(ConnectionType type) {
    this.type = type;
  }
}

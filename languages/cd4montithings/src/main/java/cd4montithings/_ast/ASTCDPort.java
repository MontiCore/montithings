package cd4montithings._ast;

import de.monticore.types.check.SymTypeExpression;

import java.util.Optional;

public class ASTCDPort extends ASTCDPortTOP {
  private Optional<SymTypeExpression> type;

  protected ASTCDPort() {
    super();
    type = Optional.empty();
  }

  public boolean isPresentType() {
    return type.isPresent();
  }

  public SymTypeExpression getType() {
    return type.get();
  }

  public void setType(SymTypeExpression type) {
    this.type = Optional.of(type);
  }
}

// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

import arcbasis._symboltable.PortSymbol;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

public class ASTPortTemplateTag extends ASTPortTemplateTagTOP {

  PortSymbol portSymbol;

  public PortSymbol getPortSymbol() {
    return portSymbol;
  }

  public void setPortSymbol(PortSymbol portSymbol) {
    this.portSymbol = portSymbol;
  }

  @Override public String getName() {
    return getPort();
  }

  public List<mtconfig._ast.ASTHookpoint> getHookpointList() {
    return getSinglePortTagList().stream()
      .filter(e -> e instanceof mtconfig._ast.ASTHookpoint)
      .map(e -> (mtconfig._ast.ASTHookpoint) e)
      .collect(Collectors.toList());
  }

  public boolean hasEveryTag() {
    return !getSinglePortTagList().stream()
      .filter(e -> e instanceof ASTEveryTag)
      .map(e -> (ASTEveryTag) e)
      .collect(Collectors.toSet()).isEmpty();
  }

  public Optional<ASTEveryTag> getEveryTag() {
    return getSinglePortTagList().stream()
      .filter(e -> e instanceof ASTEveryTag)
      .map(e -> (ASTEveryTag) e)
      .findFirst();
  }
}

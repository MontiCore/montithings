/* generated from model MontiArc*/
/* generated by template ast.AstClass*/

package montiarc._ast;

import java.util.List;
import java.util.stream.Collectors;

public class ASTInitialStateDeclaration extends ASTInitialStateDeclarationTOP {

  public ASTInitialStateDeclaration() {
    super();
  }
  
  public ASTInitialStateDeclaration(List<String> names, ASTBlock block) {
    super(names,block);
  }

  public String getName() {
    String syntheticName = this.getNames().stream().map(Object::toString).collect(Collectors.joining("."));
    return syntheticName;
  }
  
}
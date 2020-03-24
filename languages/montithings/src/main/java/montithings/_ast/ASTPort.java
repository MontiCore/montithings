/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import de.monticore.types.types._ast.ASTType;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTStereotype;
import montiarc._ast.MontiArcNodeFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @since 20.03.20
 */
public class ASTPort extends ASTPortTOP {

  public ASTPort() {
  }

  public ASTPort(Optional<ASTStereotype> stereotype,
      ASTType type,
      Optional<ASTAllowedValues> allowedValues, List<ASTMTPortDeclaration> mTPortDeclarations,
      boolean incoming, boolean outgoing) {
    super(stereotype, type, allowedValues, mTPortDeclarations, incoming, outgoing);
  }

  public montiarc._ast.ASTPort asAstPort() {
    List<String> names = mTPortDeclarations.stream()
        .map(ASTMTPortDeclaration::getName)
        .collect(Collectors.toList());
    return MontiArcNodeFactory.createASTPort(stereotype, type, names, incoming, outgoing);
  }
}

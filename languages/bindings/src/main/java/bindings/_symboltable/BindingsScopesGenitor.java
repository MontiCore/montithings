// (c) https://github.com/MontiCore/monticore
package bindings._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import bindings._ast.ASTBindingsCompilationUnit;

import java.util.Optional;

public class BindingsScopesGenitor extends BindingsScopesGenitorTOP {

  @Override protected void initArtifactScopeHP1(IBindingsArtifactScope scope) {
    scope.setName("");
  }

  @Override public void visit(ASTBindingRule node) {
    super.visit(node);
    if (node.isPresentInterfaceComponent()) {
      String interCompName = node.getInterfaceComponent().getQName();
      Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(interCompName);
      comp.ifPresent(node::setInterfaceComponentSymbol);
    }

    if (node.isPresentInterfaceInstance()) {
      String interInstanceName = node.getInterfaceInstance().getQName();
      Optional<ComponentInstanceSymbol> comp = node.getEnclosingScope().resolveComponentInstance(interInstanceName);
      comp.ifPresent(node::setInterfaceInstanceSymbol);
    }

    String implComp = node.getImplementationComponent().getQName();
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(implComp);
    comp.ifPresent(node::setImplementationComponentSymbol);
  }
}

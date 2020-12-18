// (c) https://github.com/MontiCore/monticore
package bindings._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;

public class BindingsSymbolTableCreator extends BindingsSymbolTableCreatorTOP {

  @Override public void visit(ASTBindingRule node) {
    super.visit(node);
    if (node.isPresentInterfaceComponent()) {
      String interCompName = node.getInterfaceComponent().getQName();
      ComponentTypeSymbol comp = node.getEnclosingScope().resolveComponentType(interCompName).get();
      node.setInterfaceComponentSymbol(comp);
    }

    if (node.isPresentInterfaceInstance()) {
      String interCompName = node.getInterfaceInstance().getQName();
      ComponentInstanceSymbol comp = node.getEnclosingScope().resolveComponentInstance(interCompName).get();
      node.setInterfaceInstanceSymbol(comp);
    }

    String interCompName = node.getImplementationComponent().getQName();
    ComponentTypeSymbol comp = node.getEnclosingScope().resolveComponentType(interCompName).get();
    node.setImplementationComponentSymbol(comp);
  }
}

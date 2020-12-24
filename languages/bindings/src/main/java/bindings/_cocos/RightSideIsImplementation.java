// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;

/**
 * Checks that Right Side of Binding is Implementation
 *
 * @author Julian Krebber
 */
public class RightSideIsImplementation implements BindingsASTBindingRuleCoCo {

  // Input: BindingRule with form "Interface -> Implementation;"
  @Override
  public void check(ASTBindingRule node) {

    new ImplementationExists().check(node);

    String implComp = node.getImplementationComponent().getQName();
    ComponentTypeSymbol comp = node.getEnclosingScope().resolveComponentType(implComp).get();

    if(!(comp.getAstNode() instanceof ASTMTComponentType)){
      Log.error(String.format(BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION.toString(),
        comp.getFullName()));
      return;
    }

    ASTMTComponentType ast = (ASTMTComponentType) comp.getAstNode();

    if (ast.getMTComponentModifier().isInterface()){
      Log.error(String.format(BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION.toString(),
        comp.getFullName()));
    }
  }
}
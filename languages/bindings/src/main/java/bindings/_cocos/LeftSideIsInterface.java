// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

import arcbasis._ast.ASTComponentType;
import bindings._ast.ASTBindingRule;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentModifier;
import montithings._ast.ASTMTComponentType;

/**
 * Checks that Left Side of Binding is Interface
 *
 * @author Julian Krebber
 */
public class LeftSideIsInterface implements BindingsASTBindingRuleCoCo {

  @Override
  public void check(ASTBindingRule node) {

    new InterfaceExists().check(node);

    ASTComponentType component = node.isInstance() ?
      node.getInterfaceInstanceSymbol().getType().getAstNode() :
      node.getInterfaceComponentSymbol().getAstNode();

    if (!(component instanceof ASTMTComponentType)) {
      Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString(), component.getName()));
      return;
    }

    ASTMTComponentModifier modifier = ((ASTMTComponentType) component).getMTComponentModifier();

    if (!modifier.isInterface()) {
      Log.error(String.format(BindingsError.LEFT_SIDE_NO_INTERFACE.toString(), component.getName()));
    }
  }
}
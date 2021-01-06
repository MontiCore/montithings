// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import montithings._visitor.MontiThingsVisitor;
import montithings.generator.helper.TypesHelper;

/**
 * Finds all type arguments with which a component type is ever instantiated in the architecture
 *
 * @since 05.01.21
 */
public class GenericInstantiationVisitor implements MontiThingsVisitor {

  Multimap<ComponentTypeSymbol, String> typeArguments = ArrayListMultimap.create();

  @Override public void visit(ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(),
      "ASTComponentInstance node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentInstanceSymbol instance = node.getSymbol();
    final ComponentTypeSymbol component = instance.getType();

    String typeArgs = TypesHelper.getTypeArguments(instance);

    if (!typeArgs.equals("")) {
      typeArguments.put(component, typeArgs);
    }
  }

  public Multimap<ComponentTypeSymbol, String> getTypeArguments() {
    return typeArguments;
  }
}

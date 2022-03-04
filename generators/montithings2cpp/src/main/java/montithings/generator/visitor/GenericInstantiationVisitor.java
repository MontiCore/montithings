// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;
import montithings.generator.helper.TypesHelper;
import montithings.util.IdentifierUtils;

/**
 * Finds all type arguments with which a component type is ever instantiated in the architecture
 *
 * @since 05.01.21
 */
public class GenericInstantiationVisitor
  implements ArcBasisVisitor2, ArcBasisHandler {

  ArcBasisTraverser traverser;

  Multimap<ComponentTypeSymbol, String> typeArguments = ArrayListMultimap.create();

  @Override public void visit(ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(),
      "ASTComponentInstance node '%s' has no symbol. "
        + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentInstanceSymbol instance = node.getSymbol();
    final ComponentTypeSymbol component = IdentifierUtils.resolveComponentTypeSymbolSurrogate(instance.getType());

    String typeArgs = TypesHelper.getTypeArguments(instance);

    if (!typeArgs.equals("")) {
      typeArguments.put(component, typeArgs);
    }
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4ArcBasis(this);
    traverser.setArcBasisHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Multimap<ComponentTypeSymbol, String> getTypeArguments() {
    return typeArguments;
  }

  @Override public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }
}

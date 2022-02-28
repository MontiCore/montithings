// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings._symboltable.MontiThingsScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helpful methods for using component bindings by generics
 */
public class GenericBindingUtil {

  protected GenericBindingUtil() {

  }

  /**
   * Returns mapping between the generic name and the interface component.
   *
   * @param node component with generic parameters
   * @return mapping of generic type to its upper bound.
   * Component xy<T extends InterfaceComponent> results in mapping T->InterfaceComponent.
   */
  public static Map<String, String> getGenericBindings(ASTComponentType node) {
    Map<String, String> genericToInterface = new HashMap<>();
    if (node.getHead() instanceof ASTGenericComponentHead
      && ((ASTGenericComponentHead) node.getHead()).getArcTypeParameterList().size() > 0) {
      List<ASTArcTypeParameter> generics = ((ASTGenericComponentHead) node.getHead())
        .getArcTypeParameterList();
      for (ASTArcTypeParameter generic : generics) {
        genericToInterface.putAll(linkGenericWithInterface(generic));
      }
    }
    return genericToInterface;
  }

  /**
   * Converts generic with upper bound list to a mapping generic name -> upper bounds.
   *
   * @param generic name and upper bounds that will be returned as mapping
   * @return mapping between generic and its upper bounds if upper bounds are present, otherwise an empty mapping
   */
  protected static Map<String, String> linkGenericWithInterface(ASTArcTypeParameter generic) {
    Map<String, String> genericToInterface = new HashMap<>();
    String typeName = generic.getName();
    for (ASTMCType type : generic.getUpperBoundList()) {
      genericToInterface.put(typeName, printSimpleType(type));
    }
    return genericToInterface;
  }

  /**
   * Get component by includes and name.
   *
   * @param compWithIncludes component containing include statements that are used for name resolve.
   * @param componentToGet   component name.
   * @return component symbol with given simple name if found, else null.
   */
  public static ComponentTypeSymbol getComponentFromString(
    MontiThingsArtifactScope compWithIncludes, String componentToGet) {
    Optional<ComponentTypeSymbol> componentTypeSymbol;
    MontiThingsScope globalScope = getGlobalScope(compWithIncludes);
    if (globalScope == null)
      return null;
    componentTypeSymbol = globalScope
      .resolveComponentType(compWithIncludes.getPackageName() + "." + componentToGet);
    if (componentTypeSymbol.isPresent())
      return componentTypeSymbol.get();
    for (ImportStatement i : compWithIncludes.getImportsList()) {
      if (i.isStar()) {
        componentTypeSymbol = globalScope
          .resolveComponentType(i.getStatement() + "." + componentToGet);
      }
      else if (i.getStatement().endsWith("." + componentToGet)) {
        componentTypeSymbol = globalScope.resolveComponentType(i.getStatement());
      }
      else {
        continue;
      }
      if (componentTypeSymbol.isPresent())
        return componentTypeSymbol.get();
    }
    return null;
  }

  /**
   * Checks if a component can formally implement an interface component, by
   * checking if a component has simmilar ports as another component.
   *
   * @param implementComp component implementing an interface component.
   * @param interfaceComp interface component.
   * @return if all ports match.
   */
  public static boolean canImplementInterface(ComponentTypeSymbol implementComp,
    ComponentTypeSymbol interfaceComp) {
    return portsMatch(implementComp, interfaceComp) && portsMatch(interfaceComp, implementComp);
  }

  /**
   * Checks if each port of componentSymbol1 is similar to any port of componentSymbol2.
   * Note that the check is unidirectional, so if all ports should match between both component,
   * checkIfPortsMatch has to be called a second time with swapped arguments.
   *
   * @param componentSymbol1 first component containing ports, that all need to match any port in componentSymbol2 by name.
   * @param componentSymbol2 second component containing ports, that are used for matching by name.
   */
  public static boolean portsMatch(ComponentTypeSymbol componentSymbol1,
    ComponentTypeSymbol componentSymbol2) {
    List<PortSymbol> interfacePortSymbols = componentSymbol1.getAllPorts();
    for (PortSymbol s : interfacePortSymbols) {
      Optional<PortSymbol> similarS = componentSymbol2.getPort(s.getName());
      if (!similarS.isPresent() || s.isIncoming() != similarS.get().isIncoming()) {
        return false;
      }
      /*
       * TODO: use the commented out version once it is clear why print() uses
       * the fully qualified name and why its prefixed with the component's
       * fully qualified name
       */
      //else if (!s.getType().print().equals(similarS.get().getType().print())){
      else if (!s.getTypeInfo().getName().equals(similarS.get().getTypeInfo().getName())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Get enclosing GlobalScope.
   *
   * @param s subscope of a GlobalScope.
   * @return GlobalScope if present, or else null.
   */
  protected static MontiThingsGlobalScope getGlobalScope(MontiThingsScope s) {
    while (!(s instanceof MontiThingsGlobalScope)) {
      if (s.getEnclosingScope() == null) {
        return null;
      }
      s = (MontiThingsScope) s.getEnclosingScope();
    }
    return (MontiThingsGlobalScope) s;
  }

  /**
   * Get enclosing MontiArcArtifactScope.
   *
   * @param s subscope of a MontiArcArtifactScope.
   * @return MontiArcArtifactScope if present, or else null.
   */
  public static MontiThingsArtifactScope getEnclosingMontiArcArtifactScope(MontiThingsScope s) {
    while (!(s instanceof MontiThingsArtifactScope)) {
      if (s.getEnclosingScope() == null) {
        return null;
      }
      s = (MontiThingsScope) s.getEnclosingScope();
    }
    return (MontiThingsArtifactScope) s;
  }

  public static String printSimpleType(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) type).printWithoutTypeArguments();
    }
    else if (type instanceof ASTSIUnitType) {
      // TODO go with a more generic way, dont know if this holds true for all situations
      return ((ASTSIUnitType) type).getSIUnit().getSIUnitPrimitive().getSIUnitWithPrefix()
        .getName();
      //return type.printType(new SIUnitTypes4ComputingPrettyPrinter(new IndentPrinter()));
    }
    else {
      return type.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    }
  }
}

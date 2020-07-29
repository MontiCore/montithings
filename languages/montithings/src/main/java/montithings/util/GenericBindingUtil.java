/* (c) https://github.com/MontiCore/monticore */
package montithings.util;

import arcbasis._ast.ASTComponentType;
import de.monticore.symboltable.ImportStatement;
import montiarc._symboltable.MontiArcArtifactScope;
import java.util.HashMap;


/**
 * Helpful methods for using component bindings by generics
 *
 * @author Julian Krebber
 * @version , 01.05.2020
 * @since 5.0.2
 */
public class GenericBindingUtil {

  private GenericBindingUtil(){

  }

  /**
   * Returns mapping between the generic name and the interface component.
   * @param node component with generic parameters
   * @return mapping of generic type to its upper bound.
   * Component xy<T extends InterfaceComponent> results in mapping T->InterfaceComponent.
   */
  /*public static Map<String, ASTSimpleReferenceType> getGenericBindings(ASTComponentType node){
    Map<String,ASTSimpleReferenceType> genericToInterface = new HashMap<>();
    if (node.getHead().getArcParameterList().getGenericTypeParametersOpt().isPresent()) {
      List<ASTTypeVariableDeclaration> generics = node.getHead().getGenericTypeParameters().getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration generic : generics) {
        genericToInterface.putAll(linkGenericWithInterface(generic));
      }
    }
    return genericToInterface;
  }*/

  /**
   * Converts generic with upper bound list to a mapping generic name -> upper bounds.
   * @param generic name and upper bounds that will be returned as mapping
   * @return mapping between generic and its upper bounds if upper bounds are present, otherwise an empty mapping
   */
  /*private static Map<String, ASTSimpleReferenceType> linkGenericWithInterface(ASTTypeVariableDeclaration generic) {
    Map<String,ASTSimpleReferenceType> genericToInterface = new HashMap<>();
    String typeName = generic.getName();
    for (ASTComplexReferenceType complexReferenceType : generic.getUpperBoundList()) {
      ASTSimpleReferenceType interfaceComp = complexReferenceType.getSimpleReferenceType(0);
      genericToInterface.put(typeName, interfaceComp);
    }
    return genericToInterface;
  }*/

  /**
   * Get component by includes and name.
   * @param compWithIncludes component containing include statements that are used for name resolve.
   * @param componentToGet component name.
   * @return component symbol with given simple name if found, else null.
   */
  /*public static ComponentSymbol getComponentFromString(MontiArcArtifactScope compWithIncludes, String componentToGet){
    Optional<ComponentSymbol> componentSymbol;
    Scope globalScope = getGlobalScope(compWithIncludes);
    if (globalScope == null)
      return null;
    componentSymbol = globalScope.resolve(compWithIncludes.getPackageName()+ "." + componentToGet, ComponentSymbol.KIND);
    if (componentSymbol.isPresent())
      return componentSymbol.get();
    for (ImportStatement i : compWithIncludes.getImports()) {
      if(i.isStar()) {
        componentSymbol = globalScope.resolve(i.getStatement() + "." + componentToGet, ComponentSymbol.KIND);
      }
      else if(i.getStatement().endsWith("."+componentToGet)){
        componentSymbol = globalScope.resolve(i.getStatement(), ComponentSymbol.KIND);
      }
      else {
        continue;
      }
      if (componentSymbol.isPresent())
        return componentSymbol.get();
    }
    return null;
  }*/

  /**
   * Get enclosing GlobalScope.
   * @param s subscope of a GlobalScope.
   * @return GlobalScope if present, or else null.
   */
  /*private static Scope getGlobalScope(Scope s) {
    while(!(s instanceof GlobalScope)){
      if(!s.getEnclosingScope().isPresent()){
        return null;
      }
      s = s.getEnclosingScope().get();
    }
    return s;
  }*/

  /**
   * Get enclosing MontiArcArtifactScope.
   * @param s subscope of a MontiArcArtifactScope.
   * @return MontiArcArtifactScope if present, or else null.
   */
  /*public static MontiArcArtifactScope getEnclosingMontiArcArtifactScope(Scope s){
    while(!(s instanceof MontiArcArtifactScope)){
      if(!s.getEnclosingScope().isPresent()){
        return null;
      }
      s = s.getEnclosingScope().get();
    }
    return (MontiArcArtifactScope)s;
  }*/

  /**
   * Gets the type name of a given subComponent.
   *
   * @param comp Component that contains the subComponent.
   * @param name SubComponent instance, that identifies the subComponent by Name.
   * @return The Type of the AST subComponent that uses given instance name if present. Otherwise null.
   */
  /*public static String getSubComponentType(ASTComponentType comp, ComponentInstanceSymbol name){
    for(ASTSubComponent subComponent : comp.getSubComponents()) {
      if(subComponent.getType() instanceof ASTSimpleReferenceType) {
        if(subComponent.getInstancesList().stream().anyMatch(a ->name.getName().equals(a.getName()))) {
          return ((ASTSimpleReferenceType)subComponent.getType()).getName(0);
        }
      }
    }
    return null;
  }*/
}

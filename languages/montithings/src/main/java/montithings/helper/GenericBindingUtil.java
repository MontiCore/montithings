/* (c) https://github.com/MontiCore/monticore */
package montithings.helper;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcArtifactScope;
import montithings._ast.ASTComponent;

import java.util.*;

/**
 * Helpful methods for using component bindings by generics
 *
 * @author Julian Krebber
 * @version , 01.05.2020
 * @since
 */
public class GenericBindingUtil {

  /**
   * Returns mapping between the generic name and the interface component.
   * @param node component with generic parameters
   * @return mapping of generic type to its upper bound.
   * Component xy<T extends InterfaceComponent> results in mapping T->InterfaceComponent.
   */
  public static Map<String, ASTSimpleReferenceType> getGenericBindings(ASTComponent node){
    Map<String,ASTSimpleReferenceType> genericToInterface = new HashMap<>();
    if (node.getHead().getGenericTypeParametersOpt().isPresent()) {
      List<ASTTypeVariableDeclaration> generics = node.getHead().getGenericTypeParameters().getTypeVariableDeclarationList();
      for (int i = 0; i < generics.size(); i++) {
        if(generics.get(i).getUpperBoundList().size()>0){
          String typeName = generics.get(i).getName();
          for(ASTComplexReferenceType complexReferenceType : generics.get(i).getUpperBoundList()) {
            ASTSimpleReferenceType interfaceComp = complexReferenceType.getSimpleReferenceType(0);
            genericToInterface.put(typeName, interfaceComp);
          }
        }
      }
    }
    return genericToInterface;
  }

  /**
   * Get component by includes and name.
   * @param compWithIncludes component containing include statements that are used for name resolve.
   * @param componentToGet component name.
   * @return component symbol with given simple name if found, else null.
   */
  public static ComponentSymbol getComponentFromString(MontiArcArtifactScope compWithIncludes, String componentToGet){
    Optional<ComponentSymbol> componentSymbol;
    Scope globalScope = compWithIncludes;
    while(!(globalScope instanceof GlobalScope)){
      if(!globalScope.getEnclosingScope().isPresent()){
        return null;
      }
      globalScope = globalScope.getEnclosingScope().get();
    }
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
  }

  /**
   * Get enclosing MontiArcArtifactScope.
   * @param s subscope of a MontiArcArtifactScope.
   * @return MontiArcArtifactScope if present, or else null.
   */
  public static MontiArcArtifactScope getEnclosingMontiArcArtifactScope(Scope s){
    Optional<Scope> sc;
    while(!(s instanceof MontiArcArtifactScope)){
      if(!s.getEnclosingScope().isPresent()){
        return null;
      }
      s = s.getEnclosingScope().get();
    }
    return (MontiArcArtifactScope)s;
  }
}

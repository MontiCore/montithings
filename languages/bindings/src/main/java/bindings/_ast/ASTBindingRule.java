// (c) https://github.com/MontiCore/monticore
package bindings._ast;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentInstanceSymbolLoader;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolLoader;
import de.se_rwth.commons.logging.Log;

/**
 * AST that provides necessary binding information between MontiThings components/instances.
 */
public   class ASTBindingRule extends ASTBindingRuleTOP {

  protected  ComponentTypeSymbolLoader interfaceComponentSymbolLoader;

  protected ComponentInstanceSymbolLoader interfaceInstanceSymbolLoader;

  protected  ComponentTypeSymbolLoader implementationComponentSymbolLoader;

  public ComponentTypeSymbol getInterfaceComponentSymbol ()  {
    updateInterfaceComponentSymbolLoader();
    if (interfaceComponentSymbolLoader.isSymbolLoaded() && interfaceComponentSymbolLoader.getName() != null && interfaceComponentSymbolLoader.getEnclosingScope() != null) {
      return interfaceComponentSymbolLoader.getLoadedSymbol();
    }
    Log.error("0xA7003x411650880 interfaceComponentSymbol can't return a value. It is empty.");
    throw new IllegalStateException();

  }

  public  boolean isPresentInterfaceComponentSymbol ()  {
    updateInterfaceComponentSymbolLoader();
    if (interfaceComponentSymbolLoader.getName() != null && interfaceComponentSymbolLoader.getEnclosingScope() != null) {
      return interfaceComponentSymbolLoader.isSymbolLoaded();
    }
    return false;
  }

  protected  void updateInterfaceComponentSymbolLoader ()  {
    if (interfaceComponentSymbolLoader == null) {
      interfaceComponentSymbolLoader = new ComponentTypeSymbolLoader(this.getInterfaceComponent().getQName(), this.getEnclosingScope());
    } else {
      if (getInterfaceComponent() != null && !getInterfaceComponent().getQName().equals(interfaceComponentSymbolLoader.getName())) {
        interfaceComponentSymbolLoader.setName(getInterfaceComponent().getQName());
      } else if (getInterfaceComponent() == null && interfaceComponentSymbolLoader.getName() != null) {
        interfaceComponentSymbolLoader.setName(null);
      }
      if (getEnclosingScope() != null && !getEnclosingScope().equals(interfaceComponentSymbolLoader.getEnclosingScope())) {
        interfaceComponentSymbolLoader.setEnclosingScope(getEnclosingScope());
      }else if (getEnclosingScope() == null && interfaceComponentSymbolLoader.getEnclosingScope() != null) {
        interfaceComponentSymbolLoader.setEnclosingScope(null);
      }
    }
  }

  public ASTComponentType getInterfaceComponentDefinition ()  {
    if (isPresentInterfaceComponentDefinition()) {
      return getInterfaceComponentSymbol().getAstNode();
    }
    Log.error("0xA7003x308418005 interfaceComponentDefinition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public  boolean isPresentInterfaceComponentDefinition ()  {
    return isPresentInterfaceComponentSymbol() && getInterfaceComponentSymbol().isPresentAstNode();
  }

  public ComponentInstanceSymbol getInterfaceInstanceSymbol ()  {
    updateInterfaceInstanceSymbolLoader();
    if (interfaceInstanceSymbolLoader.isSymbolLoaded() && interfaceInstanceSymbolLoader.getName() != null && interfaceInstanceSymbolLoader.getEnclosingScope() != null) {
      return interfaceInstanceSymbolLoader.getLoadedSymbol();
    }
    Log.error("0xA7003x411650880 interfaceInstanceSymbol can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();

  }

  public  boolean isPresentInterfaceInstanceSymbol ()  {
    updateInterfaceInstanceSymbolLoader();
    if (interfaceInstanceSymbolLoader.getName() != null && interfaceInstanceSymbolLoader.getEnclosingScope() != null) {
      return interfaceInstanceSymbolLoader.isSymbolLoaded();
    }
    return false;
  }

  protected  void updateInterfaceInstanceSymbolLoader ()  {
    if (interfaceInstanceSymbolLoader == null) {
      interfaceInstanceSymbolLoader = new ComponentInstanceSymbolLoader(this.getInterfaceInstance().getQName(), this.getEnclosingScope());
    } else {
      if (getInterfaceInstance() != null && !getInterfaceInstance().getQName().equals(interfaceInstanceSymbolLoader.getName())) {
        interfaceInstanceSymbolLoader.setName(getInterfaceInstance().getQName());
      } else if (getInterfaceInstance() == null && interfaceInstanceSymbolLoader.getName() != null) {
        interfaceInstanceSymbolLoader.setName(null);
      }
      if (getEnclosingScope() != null && !getEnclosingScope().equals(interfaceInstanceSymbolLoader.getEnclosingScope())) {
        interfaceInstanceSymbolLoader.setEnclosingScope(getEnclosingScope());
      }else if (getEnclosingScope() == null && interfaceInstanceSymbolLoader.getEnclosingScope() != null) {
        interfaceInstanceSymbolLoader.setEnclosingScope(null);
      }
    }
  }

  public ASTComponentInstance getInterfaceInstanceDefinition ()  {
    if (isPresentInterfaceInstanceDefinition()) {
      return getInterfaceInstanceSymbol().getAstNode();
    }
    Log.error("0xA7003x308418005 interfaceInstanceDefinition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public  boolean isPresentInterfaceInstanceDefinition ()  {
    return isPresentInterfaceInstanceSymbol() && getInterfaceInstanceSymbol().isPresentAstNode();
  }

  public ComponentTypeSymbol getImplementationComponentSymbol ()  {
    updateImplementationComponentSymbolLoader();
    if (implementationComponentSymbolLoader.isSymbolLoaded() && implementationComponentSymbolLoader.getName() != null && implementationComponentSymbolLoader.getEnclosingScope() != null) {
      return implementationComponentSymbolLoader.getLoadedSymbol();
    }
    Log.error("0xA7003x411650880 implementationComponentSymbol can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public  boolean isPresentImplementationComponentSymbol ()  {
    updateImplementationComponentSymbolLoader();
    if (implementationComponentSymbolLoader.getName() != null && implementationComponentSymbolLoader.getEnclosingScope() != null) {
      return implementationComponentSymbolLoader.isSymbolLoaded();
    }
    return false;

  }

  protected  void updateImplementationComponentSymbolLoader ()  {
    if (implementationComponentSymbolLoader == null) {
      implementationComponentSymbolLoader = new ComponentTypeSymbolLoader(this.getImplementationComponent().getQName(), this.getEnclosingScope());
    } else {
      if (getImplementationComponent() != null && !getImplementationComponent().getQName().equals(implementationComponentSymbolLoader.getName())) {
        implementationComponentSymbolLoader.setName(getImplementationComponent().getQName());
      } else if (getImplementationComponent() == null && implementationComponentSymbolLoader.getName() != null) {
        implementationComponentSymbolLoader.setName(null);
      }
      if (getEnclosingScope() != null && !getEnclosingScope().equals(implementationComponentSymbolLoader.getEnclosingScope())) {
        implementationComponentSymbolLoader.setEnclosingScope(getEnclosingScope());
      }else if (getEnclosingScope() == null && implementationComponentSymbolLoader.getEnclosingScope() != null) {
        implementationComponentSymbolLoader.setEnclosingScope(null);
      }
    }
  }

  public ASTComponentType getImplementationComponentDefinition ()  {
    if (isPresentImplementationComponentDefinition()) {
      return getImplementationComponentSymbol().getAstNode();
    }
    Log.error("0xA7003x308418005 implementationComponentDefinition can't return a value. It is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public  boolean isPresentImplementationComponentDefinition ()  {
    return isPresentImplementationComponentSymbol() && getImplementationComponentSymbol().isPresentAstNode();
  }
}

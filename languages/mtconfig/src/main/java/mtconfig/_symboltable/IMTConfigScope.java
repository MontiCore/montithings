// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.Optional;
import java.util.function.Predicate;

public  interface IMTConfigScope extends IMTConfigScopeTOP {
  default
  public Optional<HookpointSymbol> resolveHookpoint(String plattform, PortSymbol port, String hookpoint) {
    Optional<PortTemplateTagSymbol> tagSymbol = resolvePortTemplateTag(plattform, port);
    if(!tagSymbol.isPresent()){
      return Optional.empty();
    }
    return tagSymbol.get().getSpannedScope().resolveHookpointDown(hookpoint);
  }

  default
  public Optional<CompConfigSymbol> resolveCompConfig(String plattform, ComponentTypeSymbol componentTypeSymbol) {
    return resolveCompConfig(componentTypeSymbol.getFullName()+"_"+plattform,AccessModifier.ALL_INCLUSION,new CompConfigFilter(plattform,componentTypeSymbol));
  }

  default
  public Optional<PortTemplateTagSymbol> resolvePortTemplateTagDown(PortSymbol port) {
    return resolvePortTemplateTagDown(port.getName(),AccessModifier.ALL_INCLUSION,new PortFilter(port));
  }

  default
  public Optional<PortTemplateTagSymbol> resolvePortTemplateTag(String plattform, PortSymbol port) {
    if(!port.getComponent().isPresent()){
      return Optional.empty();
    }
    Optional<CompConfigSymbol> compConfig = resolveCompConfig(plattform, port.getComponent().get());
    if(!compConfig.isPresent()){
      return Optional.empty();
    }
    return compConfig.get().getSpannedScope().resolvePortTemplateTagDown(port);
  }

  class CompConfigFilter implements Predicate<CompConfigSymbol>{
    String plattform;
    ComponentTypeSymbol componentTypeSymbol;

    CompConfigFilter(String plattform,ComponentTypeSymbol componentTypeSymbol){
      this.plattform = plattform;
      this.componentTypeSymbol = componentTypeSymbol;
    }

    @Override
    public boolean test(CompConfigSymbol compConfigSymbol) {
      if(compConfigSymbol.isPresentAstNode()&&compConfigSymbol.getAstNode().isPresentNameSymbol()&&compConfigSymbol.getAstNode().getPlatform().equals(plattform)&&compConfigSymbol.getAstNode().getNameSymbol()==componentTypeSymbol){
        return true;
      }
      return false;
    }
  }

  class PortFilter implements Predicate<PortTemplateTagSymbol>{
    PortSymbol portSymbol;

    PortFilter(PortSymbol portSymbol){
      this.portSymbol = portSymbol;
    }

    @Override
    public boolean test(PortTemplateTagSymbol portTemplateTagSymbol) {
      if(portTemplateTagSymbol.isPresentAstNode()&&portTemplateTagSymbol.getAstNode().isPresentNameSymbol()&&portTemplateTagSymbol.getAstNode().getNameSymbol()==portSymbol){
        return true;
      }
      return false;
    }
  }

}

// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IMTConfigScope extends IMTConfigScopeTOP {

  /**
   * Resolves a hookpoint for a given port and platform with the given hookpoint name.
   *
   * @param platform  Name of the platform the hookpoint applies to.
   * @param port      The port that the hookpoint applies to.
   * @param hookpoint Name that identifies the type of the hookpoint.
   * @return Hookpoint that applies to the given identification, port and platform. Otherwise empty.
   */
  default Optional<HookpointSymbol> resolveHookpoint(String platform, PortSymbol port,
    String hookpoint) {
    Optional<PortTemplateTagSymbol> tagSymbol = resolvePortTemplateTag(platform, port);
    if (!tagSymbol.isPresent()) {
      return Optional.empty();
    }
    return tagSymbol.get().getSpannedScope().resolveHookpointDown(hookpoint);
  }

  /**
   * Resolves a component configuration for a given component and platform.
   *
   * @param platform            Name of the platform the configuration applies to.
   * @param componentTypeSymbol Component that the configuration applies to9.
   * @return Component configuration that applies to the given component, and platform. Otherwise empty.
   */
  default Optional<CompConfigSymbol> resolveCompConfig(String platform,
    ComponentTypeSymbol componentTypeSymbol) {
    List<CompConfigSymbol> allConfigsForComp = resolveCompConfigMany(
      componentTypeSymbol.getFullName());
    return allConfigsForComp.stream()
      .filter(config -> config.getAstNode().getPlatform().equals(platform))
      .findAny();
  }

  /**
   * Resolves a port template tag for a given port locally.
   *
   * @param port The port that the port template tag applies to.
   * @return Port template tag that applies to the given port. Otherwise empty.
   */
  default Optional<PortTemplateTagSymbol> resolvePortTemplateTagDown(PortSymbol port) {
    return resolvePortTemplateTagDown(port.getName(), AccessModifier.ALL_INCLUSION,
      new PortTemplateTagFilter(port));
  }

  /**
   * Resolves a port template tag for a given port and platform.
   *
   * @param platform Name of the platform the port template tag applies to.
   * @param port     The port that the ort template tag applies to.
   * @return Port template tag that applies to the given port and platform. Otherwise empty.
   */
  default Optional<PortTemplateTagSymbol> resolvePortTemplateTag(String platform, PortSymbol port) {
    if (!port.getComponent().isPresent()) {
      return Optional.empty();
    }
    Optional<CompConfigSymbol> compConfig = resolveCompConfig(platform, port.getComponent().get());
    if (!compConfig.isPresent()) {
      return Optional.empty();
    }
    return compConfig.get().getSpannedScope().resolvePortTemplateTagDown(port);
  }

  /**
   * Filter that accepts a CompConfigSymbol if it uses the given platform and component.
   */
  class CompConfigFilter implements Predicate<CompConfigSymbol> {
    String platform;

    ComponentTypeSymbol componentTypeSymbol;

    CompConfigFilter(String platform, ComponentTypeSymbol componentTypeSymbol) {
      this.platform = platform;
      this.componentTypeSymbol = componentTypeSymbol;
    }

    @Override
    public boolean test(CompConfigSymbol compConfigSymbol) {
      return compConfigSymbol.isPresentAstNode()
        && compConfigSymbol.getAstNode().getComponentTypeSymbol() != null
        && compConfigSymbol.getAstNode().getPlatform().equals(platform);
    }
  }

  /**
   * Filter that accepts a PortTemplateTagSymbol if it uses the given port.
   */
  class PortTemplateTagFilter implements Predicate<PortTemplateTagSymbol> {
    PortSymbol portSymbol;

    PortTemplateTagFilter(PortSymbol portSymbol) {
      this.portSymbol = portSymbol;
    }

    @Override
    public boolean test(PortTemplateTagSymbol portTemplateTagSymbol) {
      return portTemplateTagSymbol.isPresentAstNode()
        && portTemplateTagSymbol.getAstNode().getPortSymbol() != null;
    }
  }

}

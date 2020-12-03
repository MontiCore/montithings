// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._symboltable.PortSymbol;
import de.monticore.utils.Names;
import montithings.generator.codegen.ConfigParams;
import mtconfig._symboltable.PortTemplateTagSymbol;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * Helper functions for the generator (everything related to HWC an
 * similar things)
 */
public class GeneratorHelper {
  /**
   * Gets the qualified name of the handwritten port implementation if it is present.
   *
   * @param port The port for which to check for a handwritten implementation.
   * @param hwcTemplatePath path to the handwritten templates
   * @return The qualified type name of the port that is defined by given templates for the given port.
   * If no fitting templates are present Optional.empty is returned.
   */
  public static Optional<String> getPortHwcTemplateName(PortSymbol port, ConfigParams config) {
    if (portHasHwcTemplate(port, config)) {
      String packageName = Names.getQualifier(Names.getQualifier(port.getFullName()));
      String componentName = StringUtils.capitalize(Names.getSimpleName(Names.getQualifier(port.getFullName())));
      return Optional.of(packageName + "." + componentName + StringUtils.capitalize(port.getName()) + "Port");
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * Checks if there exist code templates for the port
   *
   * @param port port for which to search for code templates
   * @return true if templates exist, false otherwise
   */
  public static boolean portHasHwcTemplate(PortSymbol port, ConfigParams config) {
    String packageName = Names.getQualifier(Names.getQualifier(port.getFullName()));
    String componentName = StringUtils.capitalize(Names.getSimpleName(Names.getQualifier(port.getFullName())));
    Set<File> files = FileHelper.getPortImplementation(
        new File(config.getHwcTemplatePath() + File.separator + Names.getPathFromPackage(packageName)),
      componentName + StringUtils.capitalize(port.getName()) + "Port");
    if(!files.isEmpty()){
      return true;
    }

    Optional<PortTemplateTagSymbol> portTemplateTagSymbol = Optional.empty();
    if(!(config.getMtConfigScope()==null)) {
      portTemplateTagSymbol = config.getMtConfigScope().resolvePortTemplateTag(config.getTargetPlatform().name(), port);
    }
    if(portTemplateTagSymbol.isPresent()&&portTemplateTagSymbol.get().isPresentAstNode()&&!portTemplateTagSymbol.get().getAstNode().isEmptyHookpoints()){
      return true;
    }

    return false;
  }
}

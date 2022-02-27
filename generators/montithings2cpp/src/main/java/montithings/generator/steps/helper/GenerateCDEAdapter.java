// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.helper;

import cdlangextension._symboltable.CDLangExtensionUnitSymbol;
import cdlangextension._symboltable.ICDLangExtensionScope;
import de.se_rwth.commons.Names;
import montithings.generator.data.GeneratorToolState;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class GenerateCDEAdapter {

  public static void generateCDEAdapter(File targetFilepath, GeneratorToolState state) {
    if (state.getConfig().getCdLangExtensionScope() != null) {
      for (ICDLangExtensionScope subScope : state.getConfig().getCdLangExtensionScope()
        .getSubScopes()) {
        for (CDLangExtensionUnitSymbol unit : subScope.getCDLangExtensionUnitSymbols().values()) {
          String simpleName = unit.getAstNode().getName();
          List<String> packageName = unit.getAstNode().getPackageList();

          state.getMtg().generateAdapter(Paths.get(targetFilepath.getAbsolutePath(),
              Names.getPathFromPackage(Names.getQualifiedName(packageName))).toFile(), packageName,
            simpleName);
        }
      }
    }
  }

}

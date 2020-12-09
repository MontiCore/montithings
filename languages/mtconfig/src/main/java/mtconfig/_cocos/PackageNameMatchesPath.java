// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig.util.MTConfigError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;

/**
 * TODO
 *
 * @since 09.12.20
 */
public class PackageNameMatchesPath {
  public boolean check(@NotNull String relativeFilePath, @NotNull ASTMTConfigUnit ast) {
    String packageOfFile = Names.getPackageFromPath(Names.getPathFromFilename(relativeFilePath));
    String packageOfModel = Names.getQualifiedName(
      ast.isPresentPackage() ? ast.getPackage().getPartList() : new ArrayList<>());

    if (!packageOfFile.endsWith(packageOfModel)) {
      Log.error(String.format(MTConfigError.PACKAGENAME_MATCHES_CONFIG.toString(),
        packageOfModel,
        packageOfFile));
      return false;
    }
    return true;
  }
}

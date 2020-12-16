// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import com.google.common.io.Files;
import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig.util.MTConfigError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Set;

import static mtconfig.util.ASTMTConfigUnitUtil.findComponentNamesInConfigUnit;

/**
 * TODO
 *
 * @since 09.12.20
 */
public class FileNameMatchesReferencedComponent {

  public boolean check(@NotNull String relativeFilePath, @NotNull ASTMTConfigUnit ast) {
    String fileRoot = Files.getNameWithoutExtension(relativeFilePath);
    Set<String> referencedComponents = findComponentNamesInConfigUnit(ast);
    for (String component : referencedComponents) {
      if (!fileRoot.equals(component)) {
        Log.error(String.format(MTConfigError.FILENAME_MATCHES_CONFIG.toString(),
          component));
        return false;
      }
    }
    return true;
  }
}

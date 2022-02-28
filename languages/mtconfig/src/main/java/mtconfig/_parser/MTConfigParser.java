// (c) https://github.com/MontiCore/monticore
package mtconfig._parser;

import com.google.common.base.Preconditions;
import montiarc._parser.MontiArcParser;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.FileNameMatchesReferencedComponent;
import mtconfig._cocos.PackageNameMatchesPath;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

public class MTConfigParser extends MTConfigParserTOP {

  @Override
  public Optional<ASTMTConfigUnit> parseMTConfigUnit(@NotNull String relativeFilePath)
    throws IOException {
    Preconditions.checkArgument(relativeFilePath != null);
    Optional<ASTMTConfigUnit> optAst = super.parseMTConfigUnit(relativeFilePath);
    if (optAst.isPresent()) {
      FileNameMatchesReferencedComponent fileNameCoCo = new FileNameMatchesReferencedComponent();
      if (!fileNameCoCo.check(relativeFilePath, optAst.get())) {
        setError(true);
      }
      PackageNameMatchesPath packageNameCoCo = new PackageNameMatchesPath();
      if (!packageNameCoCo.check(relativeFilePath, optAst.get())) {
        setError(true);
      }
    }
    if (hasErrors()) {
      return Optional.empty();
    }
    return optAst;
  }

  /**
   * @see MontiArcParser#parseMACompilationUnit(String)
   */
  @Override
  public Optional<ASTMTConfigUnit> parse(@NotNull String relativeFilePath) throws IOException {
    Preconditions.checkArgument(relativeFilePath != null);
    String nonUriPath = relativeFilePath.replace("/", Matcher.quoteReplacement(File.separator));
    return parseMTConfigUnit(nonUriPath);
  }
}

// (c) https://github.com/MontiCore/monticore
package cdlangextension._ast;

import com.google.common.base.Joiner;
import de.se_rwth.commons.StringTransformations;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.collect.Iterables.transform;

/**
 * AST for Strings separated by ':'
 */
public  class ASTCDEQualifiedColonName extends ASTCDEQualifiedColonNameTOP {
  public ASTCDEQualifiedColonName() {
  }

  public ASTCDEQualifiedColonName(List<String> parts) {
    this.parts=parts;
  }

  @Override
  public  String toString()   {
    return Joiner.on(':').skipNulls().join(
      this.getPartsList().stream()
        .map(StringTransformations.TRIM_WHITESPACE)
        .collect(Collectors.toList()).stream()
        .map(StringTransformations.TRIM_DOT)
        .collect(Collectors.toList()));
  }

}
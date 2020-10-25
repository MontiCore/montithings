// (c) https://github.com/MontiCore/monticore
package cdlangextension._ast;

import com.google.common.base.Joiner;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static com.google.common.collect.Iterables.transform;

/**
 * AST for Strings seperated by ':'
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
        transform(transform(this.getPartList(),
            StringTransformations.TRIM_WHITESPACE),
            StringTransformations.TRIM_DOT));
  }

}
// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.se_rwth.commons.Names;

public class CppMCBasicTypesPrettyPrinter extends MCBasicTypesPrettyPrinter {

  protected IndentPrinter printer;

  public CppMCBasicTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTMCQualifiedName a) {
    String name = Names.getQualifiedName(a.getPartsList());
    getPrinter().print(name.replaceAll("\\.", "::"));
  }
}

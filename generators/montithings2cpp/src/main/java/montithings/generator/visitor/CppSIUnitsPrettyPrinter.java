// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;


public class CppSIUnitsPrettyPrinter extends SIUnitsPrettyPrinter {
  public CppSIUnitsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSIUnit node) {
    getPrinter().print("double");
  }
}

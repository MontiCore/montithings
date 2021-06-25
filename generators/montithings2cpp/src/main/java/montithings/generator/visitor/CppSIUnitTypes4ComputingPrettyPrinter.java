// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;

public class CppSIUnitTypes4ComputingPrettyPrinter extends SIUnitTypes4ComputingPrettyPrinter {

  public CppSIUnitTypes4ComputingPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSIUnitType4Computing node) {
    node.getMCPrimitiveType().accept(getTraverser());
  }
}

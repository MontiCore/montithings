/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.visitor;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;

public class MySIUnitLiteralsPrettyPrinter extends SIUnitLiteralsPrettyPrinter {

  public MySIUnitLiteralsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void traverse(ASTSIUnitLiteral node) {
    printer.print(SIUnitLiteralDecoder.doubleOf(node));
  }

  @Override
  public void traverse(ASTSignedSIUnitLiteral node) {
    printer.print(SIUnitLiteralDecoder.doubleOf(node));
  }
}


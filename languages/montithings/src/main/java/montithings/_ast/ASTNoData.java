/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.literals.mcliteralsbasis._visitor.MCLiteralsBasisVisitor;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._visitor.MontiArcVisitor;
import montithings._visitor.MontiThingsVisitor;

public class ASTNoData extends ASTNoDataTOP implements ASTMontiThingsNode {

  @Override
  public void accept(ExpressionsBasisVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(MontiArcVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(MontiThingsVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(MCLiteralsBasisVisitor visitor) {
    accpt(visitor);
  }

  private void accpt(MCLiteralsBasisVisitor visitor) {
    if (visitor instanceof MCCommonLiteralsPrettyPrinter) {
      MCCommonLiteralsPrettyPrinter realVisitor = (MCCommonLiteralsPrettyPrinter) visitor;
      IndentPrinter p = realVisitor.getPrinter();
      p.print("tl::nullopt");
    }
  }
}


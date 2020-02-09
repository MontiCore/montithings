/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.literals.literals._visitor.LiteralsVisitor;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._visitor.MontiArcVisitor;
import montithings._visitor.MontiThingsVisitor;

/**
 * TODO
 *
 * @author (last commit) kirchhof
 * @version , 09.02.2020
 * @since
 */
public class ASTNoData extends ASTNoDataTOP implements ASTMontiThingsNode {

  @Override
  public void accept(MCExpressionsVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(JavaDSLVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(MontiArcVisitor visitor) {
    accpt(visitor);
  }

  @Override
  public void accept(MontiThingsVisitor visitor) { accpt(visitor); }

  @Override
  public void accept(LiteralsVisitor visitor) {
    accpt(visitor);
  }

  private void accpt(LiteralsVisitor visitor) {
    if (visitor instanceof LiteralsPrettyPrinterConcreteVisitor) {
      LiteralsPrettyPrinterConcreteVisitor realVisitor = (LiteralsPrettyPrinterConcreteVisitor) visitor;
      IndentPrinter p = realVisitor.getPrinter();
      p.print("tl::nullopt");
      /*
      ASTNullLiteral node = MontiArcMill.nullLiteralBuilder().build();
      node.setEnclosingScopeOpt(this.getEnclosingScopeOpt());
      realVisitor.getRealThis().traverse(node);
      realVisitor.getRealThis().endVisit(node);

       */
    }
  }
}


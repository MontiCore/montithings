package montithings._visitor;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4math._ast.ASTSIUnitTypes4MathNode;
import de.monticore.siunittypes4math._visitor.SIUnitTypes4MathVisitor;

public class SIUnitTypePrimitivePrettyPrinter extends SIUnitPrimitivePrettyPrinter implements SIUnitTypes4MathVisitor {
  private SIUnitTypes4MathVisitor realThis;

  @Override
  public void setRealThis(SIUnitTypes4MathVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public SIUnitTypes4MathVisitor getRealThis() {
    return realThis;
  }

  public SIUnitTypePrimitivePrettyPrinter(IndentPrinter printer) {
    super(printer);
    realThis = this;
  }

  /**
   * This method prettyprints a given node from SIUnitTypes grammar.
   *
   * @param node A node from SIUnitTypes grammar.
   * @return String representation.
   */
  public static String prettyprint(ASTSIUnitTypes4MathNode node) {
    SIUnitTypePrimitivePrettyPrinter pp = new SIUnitTypePrimitivePrettyPrinter(new IndentPrinter());
    node.accept(pp);
    return pp.getPrinter().getContent();
  }
}

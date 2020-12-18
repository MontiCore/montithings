// (c) https://github.com/MontiCore/monticore
package mtconfig.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._ast.ASTProperty;
import mtconfig._ast.ASTRequirementStatement;
import mtconfig._visitor.MTConfigVisitor;

import java.util.Iterator;

/**
 * PrettyPrinter for the MTConfig Language.
 * TODO write test for the prettyPrinter and update it since the grammar changed.
 *
 * @author Julian Krebber
 */
public class MTConfigPrettyPrinter extends MCBasicTypesPrettyPrinter implements MTConfigVisitor {

  private MTConfigVisitor realThis = this;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public MTConfigPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#setRealThis(de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor)
   */
  @Override
  public void setRealThis(MTConfigVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#getRealThis()
   */
  @Override
  public MTConfigVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTMTConfigUnit a){
    if(a.isPresentPackage()) {
      this.getPrinter().print("package ");
      this.getPrinter().print(a.getPackage().getQName());
      this.getPrinter().println(";");
    }
  }

  @Override
  public void handle(ASTRequirementStatement a){
    this.getPrinter().print(" requires ");
    if(a.getPropertiessList().size()==1){
      a.getPropertiess(0).accept(this.getRealThis());
    }
    else{
      this.getPrinter().println("{");
      this.getPrinter().indent();
      Iterator<ASTProperty> iter_propertiesss = a.getPropertiessList().iterator();
      while (iter_propertiesss.hasNext()) {
        iter_propertiesss.next().accept(getRealThis());
      }
      this.getPrinter().unindent();
      this.getPrinter().println(" } ");
    }
  }

  @Override
  public void visit(ASTProperty a){
    this.getPrinter().print(a.getName() + ":");
  }

  @Override
  public void endVisit(ASTProperty a){
    this.getPrinter().println(";");
  }

}

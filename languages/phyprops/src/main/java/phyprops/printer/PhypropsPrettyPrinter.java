// (c) https://github.com/MontiCore/monticore
package phyprops.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.apache.commons.lang3.StringUtils;
import phyprops._ast.ASTPhypropsUnit;
import phyprops._ast.ASTProperty;
import phyprops._ast.ASTRequirementStatement;
import phyprops._visitor.PhypropsVisitor;

import java.util.Iterator;

/**
 * PrettyPrinter for the Phyprops Language.
 *
 * @author Julian Krebber
 */
public class PhypropsPrettyPrinter extends MCBasicTypesPrettyPrinter implements PhypropsVisitor {

  private PhypropsVisitor realThis = this;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public PhypropsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#setRealThis(de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor)
   */
  @Override
  public void setRealThis(PhypropsVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#getRealThis()
   */
  @Override
  public PhypropsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTPhypropsUnit a){
    if(!a.isEmptyPackages()) {
      this.getPrinter().print("package ");
      StringUtils.join(a.getPackageList(),".");
      this.getPrinter().println(";");
    }
  }

  @Override
  public void handle(ASTRequirementStatement a){
    if(a.isPresentPackage()){
      this.getPrinter().print(a.getPackage());
      this.getPrinter().print(".");
    }
    this.getPrinter().print(a.getName());
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

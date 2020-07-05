package phyprops.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import phyprops._visitor.PhypropsVisitor;

/**
 * PrettyPrinter for the Phyprops Language.
 *
 * @author Julian Krebber
 */
public class PhypropsPrinter extends MCBasicTypesPrettyPrinter implements PhypropsVisitor {

  private PhypropsVisitor realThis = this;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public PhypropsPrinter(IndentPrinter printer) {
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


}

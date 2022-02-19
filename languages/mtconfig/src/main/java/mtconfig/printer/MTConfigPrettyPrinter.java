// (c) https://github.com/MontiCore/monticore
package mtconfig.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._ast.ASTProperty;
import mtconfig._ast.ASTRequirementStatement;
import mtconfig._visitor.MTConfigHandler;
import mtconfig._visitor.MTConfigTraverser;

import java.util.Iterator;

/**
 * PrettyPrinter for the MTConfig Language.
 */
public class MTConfigPrettyPrinter extends MCBasicTypesPrettyPrinter implements MTConfigHandler {

  protected MTConfigTraverser traverser;

  public MTConfigPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override public MTConfigTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MTConfigTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTMTConfigUnit a) {
    if (a.isPresentPackage()) {
      this.getPrinter().print("package ");
      this.getPrinter().print(a.getPackage().getQName());
      this.getPrinter().println(";");
    }
  }

  @Override
  public void handle(ASTRequirementStatement a) {
    this.getPrinter().print(" requires ");
    if (a.getPropertiessList().size() == 1) {
      a.getPropertiess(0).accept(getTraverser());
    }
    else {
      this.getPrinter().println("{");
      this.getPrinter().indent();
      for (ASTProperty astProperty : a.getPropertiessList()) {
        astProperty.accept(getTraverser());
      }
      this.getPrinter().unindent();
      this.getPrinter().println(" } ");
    }
  }

  @Override
  public void handle(ASTProperty a) {
    this.getPrinter().print(a.getName() + ":");
    if (a.isPresentNumericValue()) {
      a.getNumericValue().accept(getTraverser());
    }
    if (a.isPresentStringValue()) {
      a.getStringValue().accept(getTraverser());
    }
    this.getPrinter().println(";");
  }
}

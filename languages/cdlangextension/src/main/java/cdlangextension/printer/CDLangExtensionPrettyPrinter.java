// (c) https://github.com/MontiCore/monticore
package cdlangextension.printer;

import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._ast.ASTDepLanguage;
import cdlangextension._visitor.CDLangExtensionHandler;
import cdlangextension._visitor.CDLangExtensionTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.apache.commons.lang3.StringUtils;

/**
 * PrettyPrinter for the CDLangExtension Language.
 *
 * @author Julian Krebber
 */
public class CDLangExtensionPrettyPrinter extends MCBasicTypesPrettyPrinter implements
  CDLangExtensionHandler {

  protected CDLangExtensionTraverser traverser;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public CDLangExtensionPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override public CDLangExtensionTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(CDLangExtensionTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Prints the name + '{' of the DepLanguage.
   *
   * @param a AST to be printed.
   */
  @Override
  public void handle(ASTDepLanguage a){
    getPrinter().println(a.getName() + " {");
    getPrinter().indent();

    for (ASTCDEImportStatement statement : a.getCDEImportStatementList()) {
      statement.accept(getTraverser());
    }

    getPrinter().unindent();
    getPrinter().println("}");
  }

  /**
   * Prints the ImportStatement in form of "from " + importSource + " import " + importClass + " as " + name + ";".
   *
   * @param a AST to be printed.
   */
  @Override
  public void traverse(ASTCDEImportStatement a){
    String s = "from " + a.getImportSource() + " import " + a.getImportClass() + " as " + a.getName() + ";";
    getPrinter().println(s);
  }

  @Override
  public void handle(ASTCDLangExtensionUnit a){
    if(!a.isEmptyPackage()) {
      this.getPrinter().print("package ");
      StringUtils.join(a.getPackageList(),".");
      this.getPrinter().println(";");
    }
  }


}

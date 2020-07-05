package cdlangextension.printer;

import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._ast.ASTDepLanguage;
import cdlangextension._visitor.CDLangExtensionVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

/**
 * PrettyPrinter for the CDLangExtension Language.
 *
 * @author Julian Krebber
 */
public class CDLangExtensionPrinter extends MCBasicTypesPrettyPrinter implements CDLangExtensionVisitor {

  private CDLangExtensionVisitor realThis = this;

  /**
   * Constructor.
   *
   * @param printer the printer to write to.
   */
  public CDLangExtensionPrinter(IndentPrinter printer) {
    super(printer);
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#setRealThis(de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor)
   */
  @Override
  public void setRealThis(CDLangExtensionVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor#getRealThis()
   */
  @Override
  public CDLangExtensionVisitor getRealThis() {
    return realThis;
  }

  /**
   * Prints the name + '{' of the DepLanguage.
   *
   * @param a AST to be printed.
   */
  @Override
  public void visit(ASTDepLanguage a){
    getPrinter().println(a.getName() + " {");
    getPrinter().indent();
  }

  /**
   * Prints '}'.
   *
   * @param a AST to be printed.
   */
  @Override
  public void endVisit(ASTDepLanguage a){
    getPrinter().unindent();
    getPrinter().println("}");
  }

  /**
   * Prints the ImportStatement in form of "from " + importSource + " import " + importClass + " as " + name + ";".
   *
   * @param a AST to be printed.
   */
  public void traverse(ASTCDEImportStatement a){
    String s = "from " + a.getImportSource() + " import " + a.getImportClass() + " as " + a.getName() + ";";
    getPrinter().println(s);
  }


}

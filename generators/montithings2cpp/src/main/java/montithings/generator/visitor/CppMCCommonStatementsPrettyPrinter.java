// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.types.check.TypeCheck;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

public class CppMCCommonStatementsPrettyPrinter extends MCCommonStatementsPrettyPrinter {
  TypeCheck tc;

  public CppMCCommonStatementsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(),
      new DeriveSymTypeOfMontiThingsCombine());
  }

  /**
   * Copy / Paste from MCCommonStatementsPrettyPrinter but additionally makes sure thenExpression
   * and elseExpression are surrounded by curly braces. This is necessary if in C++ one expression
   * is represented as multiple C++ statements
   *
   * @param a the if statement to print
   */
  @Override public void handle(ASTIfStatement a) {
    CommentPrettyPrinter.printPreComments(a, this.getPrinter());
    this.getPrinter().print("if (");
    a.getCondition().accept(this.getRealThis());
    this.getPrinter().print(") ");

    if (!(a.getThenStatement() instanceof ASTMCJavaBlock)) {
      this.getPrinter().print("{");
    }
    a.getThenStatement().accept(this.getRealThis());
    if (!(a.getThenStatement() instanceof ASTMCJavaBlock)) {
      this.getPrinter().print("}");
    }
    if (a.isPresentElseStatement()) {
      this.getPrinter().println("else ");
      if (!(a.getElseStatement() instanceof ASTMCJavaBlock)) {
        this.getPrinter().print("{");
      }
      a.getElseStatement().accept(this.getRealThis());
      if (!(a.getElseStatement() instanceof ASTMCJavaBlock)) {
        this.getPrinter().print("}");
      }
    }

    CommentPrettyPrinter.printPostComments(a, this.getPrinter());
  }
}

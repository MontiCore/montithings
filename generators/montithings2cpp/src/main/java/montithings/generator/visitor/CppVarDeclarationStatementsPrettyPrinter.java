// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import montithings.generator.helper.TypesHelper;

import java.util.Iterator;

public class CppVarDeclarationStatementsPrettyPrinter extends MCVarDeclarationStatementsPrettyPrinter {

  public CppVarDeclarationStatementsPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  @Override
  public void handle(ASTLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, this.getPrinter());
    a.getMCModifierList().stream().forEach((m) -> {
      this.getPrinter().print(" ");
      m.accept(this.getRealThis());
      this.getPrinter().print(" ");
    });
    this.getPrinter().print(" ");
    if(a.getVariableDeclarator(0).getDeclarator().getSymbol().getType() instanceof SymTypeOfNumericWithSIUnit){
      this.getPrinter().print("double");
    }
    else {
      if (a.getMCType() instanceof ASTMCQualifiedType) {
        ASTMCQualifiedType type = (ASTMCQualifiedType) a.getMCType();
        if (!type.getMCQualifiedName().getQName().contains(".")) {
          // This fixes java types that are printed as different c++ types
          String typeName = TypesHelper.java2cppTypeString(type.getMCQualifiedName().getQName());
          getPrinter().print(typeName);
        }
      }
      else {
        a.getMCType().accept(this.getRealThis());
      }
    }
    this.getPrinter().print(" ");
    String sep = "";
    Iterator var4 = a.getVariableDeclaratorList().iterator();

    while(var4.hasNext()) {
      ASTVariableDeclarator v = (ASTVariableDeclarator)var4.next();
      this.getPrinter().print(sep);
      sep = ", ";
      v.accept(this.getRealThis());
    }

    CommentPrettyPrinter.printPostComments(a, this.getPrinter());
  }
}

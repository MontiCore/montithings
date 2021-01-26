// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._ast.ASTSetValueItem;
import de.monticore.ocl.setexpressions._ast.ASTSetValueRange;
import de.monticore.prettyprint.IndentPrinter;

public class CppOCLExpressionsPrettyPrinter extends OCLExpressionsPrettyPrinter {

  public CppOCLExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  @Override
  public void handle(ASTIfThenElseExpression node) {
    getPrinter().print("if (");
    node.getCondition().accept(getRealThis());
    getPrinter().println(") {");
    node.getThenExpression().accept(getRealThis());
    getPrinter().println();
    getPrinter().println(" }");
    getPrinter().println("else {");
    node.getElseExpression().accept(getRealThis());
    getPrinter().println();
    getPrinter().println("}");
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getRealThis());
    getPrinter().print(") && (");
    node.getRight().accept(getRealThis());
    getPrinter().print(")) || ( !(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTEquivalentExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getRealThis());
    getPrinter().print(") && (");
    node.getRight().accept(getRealThis());
    getPrinter().print(")) || (( !(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")) && ( !(");
    node.getRight().accept(getRealThis());
    getPrinter().print("))))");
  }

  @Override
  public void handle (ASTForallExpression node){
    getPrinter().println("[&]() -> bool {");

    //TODO: multiple InDeclarations?
    String symbolName = node.getInDeclaration(0).getInDeclarationVariable(0).getName();
    String symbolType = node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType().getTypeInfo().getName();

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    getPrinter().println(";");

    getPrinter().println("for (int _index = 0; _index < set.size(); _index++){");
    getPrinter().print("if(!(");
    getPrinter().println("[&](" + symbolType + " " + symbolName + ") -> bool {");
    getPrinter().print("if(");

    node.getExpression().accept(getRealThis());

    getPrinter().print("){ return true;} return false;");
    getPrinter().println("}(set.at(_index))");
    getPrinter().println(")){");

    getPrinter().println("return false;");
    getPrinter().println("}");
    getPrinter().println("}");
    getPrinter().println("return true;");
    getPrinter().println("}()");
  }

  @Override
  public void handle (ASTExistsExpression node){
    getPrinter().println("[&]() -> bool {");

    //TODO: multiple InDeclarations?
    String symbolName = node.getInDeclaration(0).getInDeclarationVariable(0).getName();
    String symbolType = node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType().getTypeInfo().getName();

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    getPrinter().println(";");

    getPrinter().println("for (int _index = 0; _index < set.size(); _index++){");
    getPrinter().print("if(");
    getPrinter().println("[&](" + symbolType + " " + symbolName + ") -> bool {");
    getPrinter().print("if(");

    node.getExpression().accept(getRealThis());

    getPrinter().print("){ return true;} return false;");
    getPrinter().println("}(set.at(_index))");
    getPrinter().println("){");

    getPrinter().println("return true;");
    getPrinter().println("}");
    getPrinter().println("}");
    getPrinter().println("return false;");
    getPrinter().println("}()");
  }

  public void printSet(ASTSetEnumeration node){
    getPrinter().print("{");
    for (int i = 0; i < node.sizeSetCollectionItems(); i++){
      if (node.getSetCollectionItem(i) instanceof ASTSetValueItem){
        for (ASTExpression expr : ((ASTSetValueItem) node.getSetCollectionItem(i)).getExpressionList()){
          expr.accept(getRealThis());
          if (expr != ((ASTSetValueItem) node.getSetCollectionItem(i)).getExpression(((ASTSetValueItem)
                  node.getSetCollectionItem(i)).sizeExpressions() - 1)){
            getPrinter().print(", ");
          }
        }
      } else if (node.getSetCollectionItem(i) instanceof ASTSetValueRange){
        //TODO: SetBuilder for other SetCollection Types
      }
      if (i != node.sizeSetCollectionItems() - 1){
        getPrinter().print(", ");
      }
    }
    getPrinter().print("}");
  }
}

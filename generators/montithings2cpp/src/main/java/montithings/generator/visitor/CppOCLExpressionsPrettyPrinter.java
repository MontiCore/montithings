// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

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
    if(!node.getInDeclaration(0).isPresentExpression()){
      //TODO: no Expression present
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getInDeclaration(0).getExpression());
    }
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

  @Override
  public void handle (ASTLetinExpression node){
    getPrinter().print("[&]() -> bool {");

    List<String> varNames = new ArrayList<>();
    List<String> varTypes = new ArrayList<>();
    for (ASTOCLVariableDeclaration variableDeclaration : node.getOCLVariableDeclarationList()){
      variableDeclaration.accept(getRealThis());
      if(variableDeclaration.isPresentExpression()){
        varNames.add(variableDeclaration.getName());
        varTypes.add(variableDeclaration.getSymbol().getType().getTypeInfo().getName());
      }
      //TODO: was wenn Expression nicht present?
    }
    getPrinter().print("return ");
    getPrinter().print("[&](");
    for (int i = 0; i < varNames.size(); i++){
      getPrinter().print(varTypes.get(i) + " ");
      getPrinter().print(varNames.get(i));
      if(i != varNames.size() - 1){
        getPrinter().print(", ");
      }
    }
    getPrinter().print(") -> bool {");

    getPrinter().print("return (");
    node.getExpression().accept(getRealThis());
    getPrinter().print(");");

    getPrinter().print("}(");
    for (int i = 0; i < varNames.size(); i++){
      getPrinter().print(varNames.get(i));
      if(i != varNames.size() - 1){
        getPrinter().print(", ");
      }
    }
    getPrinter().print(")");
    getPrinter().print(";");

    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTOCLVariableDeclaration node){
    getPrinter().print(node.getSymbol().getType().getTypeInfo().getName());
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    if(node.isPresentExpression()){
      getPrinter().print(" = ");
      node.getExpression().accept(getRealThis());
    }
    getPrinter().print(";");
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

  public void printSet(ASTSetComprehension node){
    if (node.getLeft().isPresentGeneratorDeclaration()){
      if(node.getLeft().getGeneratorDeclaration().getExpression() instanceof ASTSetEnumeration){
        printSet((ASTSetEnumeration) node.getLeft().getGeneratorDeclaration().getExpression());
      } else if (node.getLeft().getGeneratorDeclaration().getExpression() instanceof ASTSetComprehension){
        //TODO: test, this case probably doesn't work yet
        printSet((ASTSetComprehension) node.getLeft().getGeneratorDeclaration().getExpression());
      } else {
        Log.error("Set building expressions other than SetEnumerations and SetComprehensions are " +
                "not supported in GeneratorDeclarations");
      }
      getPrinter().println(";");
      getPrinter().print("set.erase(std::remove_if(set.begin(), set.end(), ");
      printSetComprehensionExpressions(node);
      getPrinter().print("), set.end())");
    }
  }

  private void printSetComprehensionExpressions(ASTSetComprehension setComprehension) {
    getPrinter().print("[&] (int " + setComprehension.getLeft().getGeneratorDeclaration().getName() + ") { return ");

    for (int i = 0; i < setComprehension.sizeSetComprehensionItems(); i++){
      if(!(setComprehension.getSetComprehensionItem(i).isPresentExpression())){
        Log.error("Only expressions are supported at the right side of set comprehensions");
      }
      getPrinter().print("!(");
      setComprehension.getSetComprehensionItem(i).getExpression().accept(getRealThis());
      getPrinter().print(")");

      if (i != setComprehension.sizeSetComprehensionItems() - 1){
        getPrinter().print("||");
      }
    }
    getPrinter().print(";");
    getPrinter().print("}");
  }
}

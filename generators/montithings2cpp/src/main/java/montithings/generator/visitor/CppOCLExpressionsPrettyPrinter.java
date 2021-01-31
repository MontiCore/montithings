// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import setdefinitions._ast.ASTSetValueRange;

import java.util.ArrayList;
import java.util.List;

import static montithings.generator.helper.ComponentHelper.printCPPTypeName;

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
    String symbolType = printCPPTypeName(node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType());

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    if(!node.getInDeclaration(0).isPresentExpression()){
      //TODO: no Expression present
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getInDeclaration(0).getExpression());
    } else {
      //TODO: other expression types
    }
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
    String symbolType = printCPPTypeName(node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType());

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    if(!node.getInDeclaration(0).isPresentExpression()){
      //TODO: no Expression present
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getInDeclaration(0).getExpression());
    } else {
      //TODO: other expression types
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
        varTypes.add(printCPPTypeName(variableDeclaration.getSymbol().getType()));
      }
      //TODO: was wenn Expression nicht present?
    }
    getPrinter().print("return ");
    getPrinter().print("[&](");
    //TODO: einfacher? nicht noch eine lambda fkt aufmachen?
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
    getPrinter().print(printCPPTypeName(node.getSymbol().getType()));
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    if(node.isPresentExpression()){
      getPrinter().print(" = ");
      node.getExpression().accept(getRealThis());
    }
    getPrinter().print(";");
  }

  @Override
  public void handle (ASTTypeIfExpression node){
    getPrinter().print("if (");
    getPrinter().print("std::is_base_of<");
    node.getMCType().accept(getRealThis());
    getPrinter().print(", " + node.getName() + ">) {");
    //TODO: cast to Base Type
    node.getThenExpression().accept(getRealThis());
    getPrinter().print("} else {");
    node.getElseExpression().accept(getRealThis());
    getPrinter().print("}");
  }

  @Override
  public void handle (ASTInstanceOfExpression node){
    getPrinter().print("[&]() -> bool {");
    getPrinter().print("return ");
    getPrinter().print("std::is_base_of<");
    //TODO: naechste Zeile testen
    node.getMCType().accept(getRealThis());
    getPrinter().print(", " + node.getExpression() + ">;");
    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTIterateExpression node){
    String type = printCPPTypeName(node.getInit().getSymbol().getType());
    getPrinter().print("[&]() -> " + type + "{");
    getPrinter().print("std::vector<" + type + "> set = ");
    if (node.getIteration().getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getIteration().getExpression());
    }
    else if (node.getIteration().getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getIteration().getExpression());
    }
    else {
      Log.error("Only SetEnumerations or SetComprehensions are allow  ed in the Iterator of IterateExpressions");
    }
    getPrinter().print(";");
    node.getInit().accept(getRealThis());
    getPrinter().print("for( auto " + node.getIteration().getInDeclarationVariable(0).getName() + " : set) {");
    getPrinter().print(node.getName() + " = ");
    node.getValue().accept(getRealThis());
    getPrinter().print(";");
    getPrinter().print("}");
    getPrinter().print("return " + node.getName() + ";");
    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTAnyExpression node){
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    String type = printCPPTypeName(tc.typeOf(node));
    getPrinter().print("[&]() -> " + type + "{");
    getPrinter().print("std::vector<" + type + "> set = ");
    if (node.getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getExpression());
    }
    else if (node.getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getExpression());
    }
    else {
      Log.error("Only SetEnumerations or SetComprehensions are allowed in AnyExpressions");
    }
    getPrinter().print("return set.at(0);");
    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTTypeCastExpression node){
    //TODO: special handling of SI Types
    super.handle(node);
  }

  @Override
  public void handle (ASTOCLAtPreQualification node){
    if(!(node.getExpression() instanceof ASTNameExpression)) {
      Log.error("OCLAtPreQualification can only be applied to variables of components");
    }
    else {
      node.getExpression().accept(getRealThis());
      getPrinter().print("__at__pre");
    }
  }

  public void printSet(ASTSetEnumeration node){
    getPrinter().print("{");
    List<ASTSetValueRange> elementsToAdd = new ArrayList<>();
    for (int i = 0; i < node.sizeSetCollectionItems(); i++){
      if (node.getSetCollectionItem(i) instanceof ASTSetValueItem){
        for (ASTExpression expr : ((ASTSetValueItem) node.getSetCollectionItem(i)).getExpressionList()){
          expr.accept(getRealThis());
          if (expr != ((ASTSetValueItem) node.getSetCollectionItem(i)).getExpression(((ASTSetValueItem)
                  node.getSetCollectionItem(i)).sizeExpressions() - 1)){
            getPrinter().print(", ");
          }
        }
      }
      else if (node.getSetCollectionItem(i) instanceof setdefinitions._ast.ASTSetValueRange){
        elementsToAdd.add((ASTSetValueRange) node.getSetCollectionItem(i));
      }
      else {
        Log.error("only SetValueItems and SetValueRanges are supported in InDeclaration SetDefinitions");
      }
      if (i != node.sizeSetCollectionItems() - 1){
        getPrinter().print(", ");
      }
    }
    getPrinter().print("}");

    //print SetValueRanges if enumeration uses them
    for (ASTSetValueRange range : elementsToAdd){
      getPrinter().print(";");
      getPrinter().print("for (int _set_value_range_index = ");
      range.getLowerBound().accept(getRealThis());
      getPrinter().print("; _set_value_range_index <=");
      range.getUpperBound().accept(getRealThis());
      getPrinter().print("; _set_value_range_index++){");
      if(range.isPresentStepsize()){
        getPrinter().print("if((_set_value_range_index - (");
        range.getLowerBound().accept(getRealThis());
        getPrinter().print(")) % ");
        range.getStepsize().accept(getRealThis());
        getPrinter().print(" == 0){");
        getPrinter().print("set.push_back(_set_value_range_index);");
        getPrinter().print("}");
        //wenn ich mit lowerBound addiere / stepSize teilbar
      }
      else {
        getPrinter().print("set.push_back(_set_value_range_index);");
      }
      getPrinter().print("}");
    }
  }

  public void printSet(ASTSetComprehension node){
    if (node.getLeft().isPresentGeneratorDeclaration()){
      if(node.getLeft().getGeneratorDeclaration().getExpression() instanceof ASTSetEnumeration){
        printSet((ASTSetEnumeration) node.getLeft().getGeneratorDeclaration().getExpression());
      } else {
        Log.error("Set building expressions other than SetEnumerations are " +
                "not supported in GeneratorDeclarations of SetComprehensions");
      }
      getPrinter().println(";");
      getPrinter().print("set.erase(std::remove_if(set.begin(), set.end(), ");
      printSetComprehensionExpressions(node);
      getPrinter().print("), set.end())");
    }
    else {
      //TODO: support SetVariableDeclaration as well (?)
      Log.error("SetComprehensions in InDeclarations are only supported if the left side is a generator declaration");
    }
  }

  private void printSetComprehensionExpressions(ASTSetComprehension setComprehension) {
    String varName = setComprehension.getLeft().getGeneratorDeclaration().getName();
    String varType = printCPPTypeName(setComprehension.getLeft().getGeneratorDeclaration().getSymbol().getType());
    getPrinter().print("[&] (" + varType + " " + varName + ") { return ");

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

// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._ast.ASTSetValueItem;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings.generator.codegen.util.Identifier;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import setdefinitions._ast.ASTSetValueRange;

import java.util.ArrayList;
import java.util.List;

import static montithings.generator.helper.TypesPrinter.printCPPTypeName;
import static montithings.generator.prettyprinter.CppPrettyPrinterUtils.capitalize;

public class CppOCLExpressionsPrettyPrinter extends OCLExpressionsPrettyPrinter {

  public CppOCLExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTIfThenElseExpression node) {
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    String expressionType = printCPPTypeName(tc.typeOf(node));

    getPrinter().print("[&]() -> " + expressionType + " {");

    getPrinter().print("if (");
    node.getCondition().accept(getTraverser());
    getPrinter().println(") {");
    getPrinter().print("return ");
    node.getThenExpression().accept(getTraverser());
    getPrinter().println(";");
    getPrinter().println(" }");
    getPrinter().println("else {");
    getPrinter().print("return ");
    node.getElseExpression().accept(getTraverser());
    getPrinter().println(";");
    getPrinter().println("}");

    getPrinter().print("}()");
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getTraverser());
    getPrinter().print(") && (");
    node.getRight().accept(getTraverser());
    getPrinter().print(")) || ( !(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTEquivalentExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getTraverser());
    getPrinter().print(") && (");
    node.getRight().accept(getTraverser());
    getPrinter().print(")) || (( !(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(")) && ( !(");
    node.getRight().accept(getTraverser());
    getPrinter().print("))))");
  }

  @Override
  public void handle (ASTForallExpression node){
    getPrinter().println("[&]() -> bool {");

    if(node.getInDeclarationList().size() > 1){
      Log.error("Only one InDeclaration is supported for every ForallExpression");
    }
    String symbolName = node.getInDeclaration(0).getInDeclarationVariable(0).getName();
    String symbolType = printCPPTypeName(node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType());

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    if(!node.getInDeclaration(0).isPresentExpression()){
      Log.error("InDeclarations without Expressions are not supported");
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getInDeclaration(0).getExpression());
    } else {
      Log.error("Only SetEnumerations and SetComprehensions are supported as Expressions in InDeclarations");
    }
    getPrinter().println(";");

    getPrinter().println("for (int _index = 0; _index < set.size(); _index++){");
    getPrinter().print("if(!(");
    getPrinter().println("[&](" + symbolType + " " + symbolName + ") -> bool {");
    getPrinter().print("if(");

    node.getExpression().accept(getTraverser());

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

    if(node.getInDeclarationList().size() > 1){
      Log.error("Only one InDeclaration is supported for every ExistsExpression");
    }
    String symbolName = node.getInDeclaration(0).getInDeclarationVariable(0).getName();
    String symbolType = printCPPTypeName(node.getInDeclaration(0).getInDeclarationVariable(0).
            getSymbol().getType());

    getPrinter().print("std::vector<" + symbolType + "> set = ");
    if(!node.getInDeclaration(0).isPresentExpression()){
      Log.error("InDeclarations without Expressions are not supported");
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getInDeclaration(0).getExpression());
    } else if(node.getInDeclaration(0).getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getInDeclaration(0).getExpression());
    } else {
      Log.error("Only SetEnumerations and SetComprehensions are supported as Expressions in InDeclarations");
    }
    getPrinter().println(";");

    getPrinter().println("for (int _index = 0; _index < set.size(); _index++){");
    getPrinter().print("if(");
    getPrinter().println("[&](" + symbolType + " " + symbolName + ") -> bool {");
    getPrinter().print("if(");

    node.getExpression().accept(getTraverser());

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

    for (ASTOCLVariableDeclaration variableDeclaration : node.getOCLVariableDeclarationList()){
      variableDeclaration.accept(getTraverser());
    }
    getPrinter().print("return (");
    node.getExpression().accept(getTraverser());
    getPrinter().print(");");

    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTOCLVariableDeclaration node){
    getPrinter().print(printCPPTypeName(node.getSymbol().getType()));
    getPrinter().print(" ");
    getPrinter().print(node.getName());
    if(node.isPresentExpression()){
      getPrinter().print(" = ");
      node.getExpression().accept(getTraverser());
    }
    getPrinter().print(";");
  }

  @Override
  public void handle (ASTTypeIfExpression node){
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    String expressionType = printCPPTypeName(tc.typeOf(node.getThenExpression()));

    getPrinter().print("[&]() -> " + expressionType + " {");

    //TODO: test TypeIfExpression and InstanceOfExpression
    String varType = printCPPTypeName(node.getNameSymbol().getType());
    getPrinter().print("if (");
    getPrinter().print("std::is_base_of<");
    node.getMCType().accept(getTraverser());
    getPrinter().print(", " + varType + ">::value) {");
    getPrinter().print("return ");
    //TODO: cast to Base Type
    node.getThenExpression().accept(getTraverser());
    getPrinter().print("; } else {");
    getPrinter().print("return ");
    node.getElseExpression().accept(getTraverser());
    getPrinter().print("; }");

    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTInstanceOfExpression node){
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    String expressionType = printCPPTypeName(tc.typeOf(node.getExpression()));
    getPrinter().print("[&]() -> bool {");
    getPrinter().print("return ");
    getPrinter().print("std::is_base_of<");
    node.getMCType().accept(getTraverser());
    getPrinter().print(", " + expressionType + ">::value;");
    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTIterateExpression node){
    String type = printCPPTypeName(node.getInit().getSymbol().getType());
    getPrinter().print("[&]() -> " + type + "{");
    getPrinter().print("std::vector<" + type + "> set = ");
    if (!node.getIteration().isPresentExpression()){
      Log.error("InDeclarations without Expressions are not supported");
    }
    if (node.getIteration().getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getIteration().getExpression());
    }
    else if (node.getIteration().getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getIteration().getExpression());
    }
    else {
      Log.error("Only SetEnumerations or SetComprehensions are allowed in the Iterator of IterateExpressions");
    }
    getPrinter().print(";");
    node.getInit().accept(getTraverser());
    getPrinter().print("for( auto " + node.getIteration().getInDeclarationVariable(0).getName() + " : set) {");
    getPrinter().print(node.getName() + " = ");
    node.getValue().accept(getTraverser());
    getPrinter().print(";");
    getPrinter().print("}");
    getPrinter().print("return " + node.getName() + ";");
    getPrinter().print("}()");
  }

  @Override
  public void handle (ASTAnyExpression node){
    //TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    //String type = printCPPTypeName(tc.typeOf(node));
    //TODO: other types than int
    getPrinter().print("[&]() -> int {");
    getPrinter().print("std::vector<int> set = ");
    if (node.getExpression() instanceof ASTSetEnumeration){
      printSet((ASTSetEnumeration) node.getExpression());
    }
    else if (node.getExpression() instanceof ASTSetComprehension){
      printSet((ASTSetComprehension) node.getExpression());
    }
    else {
      Log.error("Only SetEnumerations or SetComprehensions are allowed in AnyExpressions");
    }
    getPrinter().print(";");
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
      ASTNameExpression name = (ASTNameExpression) node.getExpression();
      getPrinter().print(Identifier.getStateName() + "__at__pre.get");
      getPrinter().print(capitalize(name.getName()) + "()");
    }
  }

  public void printSet(ASTSetEnumeration node){
    getPrinter().print("{");
    List<ASTSetValueRange> elementsToAdd = new ArrayList<>();
    for (int i = 0; i < node.sizeSetCollectionItems(); i++){
      if (node.getSetCollectionItem(i) instanceof ASTSetValueItem){
        for (ASTExpression expr : ((ASTSetValueItem) node.getSetCollectionItem(i)).getExpressionList()){
          expr.accept(getTraverser());
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
      range.getLowerBound().accept(getTraverser());
      getPrinter().print("; _set_value_range_index <=");
      range.getUpperBound().accept(getTraverser());
      getPrinter().print("; _set_value_range_index++){");
      if(range.isPresentStepsize()){
        getPrinter().print("if((_set_value_range_index - (");
        range.getLowerBound().accept(getTraverser());
        getPrinter().print(")) % ");
        range.getStepsize().accept(getTraverser());
        getPrinter().print(" == 0){");
        getPrinter().print("set.push_back(_set_value_range_index);");
        getPrinter().print("}");
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
      Log.error("SetComprehensions in InDeclarations are only supported if the left side is a generator declaration");
    }
  }

  protected void printSetComprehensionExpressions(ASTSetComprehension setComprehension) {
    String varName = setComprehension.getLeft().getGeneratorDeclaration().getName();
    String varType = printCPPTypeName(setComprehension.getLeft().getGeneratorDeclaration().getSymbol().getType());
    getPrinter().print("[&] (" + varType + " " + varName + ") { return ");

    for (int i = 0; i < setComprehension.sizeSetComprehensionItems(); i++){
      if(!(setComprehension.getSetComprehensionItem(i).isPresentExpression())){
        Log.error("Only expressions are supported at the right side of set comprehensions");
      }
      getPrinter().print("!(");
      setComprehension.getSetComprehensionItem(i).getExpression().accept(getTraverser());
      getPrinter().print(")");

      if (i != setComprehension.sizeSetComprehensionItems() - 1){
        getPrinter().print("||");
      }
    }
    getPrinter().print(";");
    getPrinter().print("}");
  }

  @Override
  public void handle(ASTOCLArrayQualification node){
    Log.error("OCLArrayQualification is not supported");
  }

  @Override
  public void handle(ASTOCLTransitiveQualification node){
    Log.error("OCLTransitiveQualification is not supported");
  }
}

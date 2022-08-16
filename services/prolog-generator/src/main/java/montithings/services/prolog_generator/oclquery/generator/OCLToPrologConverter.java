package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.ocl.oclexpressions._ast.ASTExistsExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import montithings._visitor.MontiThingsTraverser;
import montithings._visitor.MontiThingsTraverserImplementation;
import montithings.services.prolog_generator.Utils;
import montithings.services.prolog_generator.oclquery.visitor.FindNameExpressionsVisitor;

import java.util.Set;

public class OCLToPrologConverter {

  private ExpressionsBasisToPrologPrettyPrinter expressionsBasisToPrologPrettyPrinter;
  private CommonExpressionsToPrologPrettyPrinter commonExpressionsToPrologPrettyPrinter;
  private OCLExpressionsToPrologPrettyPrinter oCLExpressionsToPrologPrettyPrinter;
  private MCBasicTypesPrettyPrinter mCBasicTypesPrettyPrinter;
  private MCCommonLiteralsPrettyPrinter mCCommonLiteralsPrettyPrinter;
  private MCBasicsPrettyPrinter mCBasicsPrettyPrinter;
  private SIUnitsPrettyPrinter siUnitsPrettyPrinter;
  private SIUnitTypes4ComputingPrettyPrinter siUnitTypes4ComputingPrettyPrinter;
  private SetDefinitionsToPrologPrettyPrinter setDefinitionsToPrologPrettyPrinter;
  private SetExpressionsToPrologPrettyPrinter setExpressionsToPrologPrettyPrinter;

  private MontiThingsTraverser traverser;
  private IndentPrinter printer;

  public OCLToPrologConverter() {
    printer = new IndentPrinter();
    traverser = new MontiThingsTraverserImplementation();

    oCLExpressionsToPrologPrettyPrinter = new OCLExpressionsToPrologPrettyPrinter(printer);
    traverser.setOCLExpressionsHandler(oCLExpressionsToPrologPrettyPrinter);

    mCBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mCBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mCBasicTypesPrettyPrinter);

    commonExpressionsToPrologPrettyPrinter = new CommonExpressionsToPrologPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsToPrologPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsToPrologPrettyPrinter);

    expressionsBasisToPrologPrettyPrinter = new ExpressionsBasisToPrologPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisToPrologPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisToPrologPrettyPrinter);

    mCBasicsPrettyPrinter = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(mCBasicsPrettyPrinter);

    mCCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(mCCommonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(mCCommonLiteralsPrettyPrinter);

    siUnitsPrettyPrinter = new SIUnitsPrettyPrinter(printer);
    traverser.setSIUnitsHandler(siUnitsPrettyPrinter);

    siUnitTypes4ComputingPrettyPrinter = new SIUnitTypes4ComputingPrettyPrinter(printer);
    traverser.setSIUnitTypes4ComputingHandler(siUnitTypes4ComputingPrettyPrinter);

    setDefinitionsToPrologPrettyPrinter = new SetDefinitionsToPrologPrettyPrinter(printer);
    traverser.setSetDefinitionsHandler(setDefinitionsToPrologPrettyPrinter);

    setExpressionsToPrologPrettyPrinter = new SetExpressionsToPrologPrettyPrinter(printer);
    traverser.setSetExpressionsHandler(setExpressionsToPrologPrettyPrinter);
  }

  public String printOCLQuery(ASTExpression node) {
    printer.clearBuffer();
    node.accept(traverser);
    return printer.getContent() + setExpressionsToPrologPrettyPrinter.getOperations();
  }

  public Set<String> getNameExpressions(ASTExpression node) {
    FindNameExpressionsVisitor nameExpressionsVisitor = new FindNameExpressionsVisitor();
    node.accept(nameExpressionsVisitor);
    return nameExpressionsVisitor.getNames();
  }

  public String getVariableForDevice(ASTExpression node) {
    if ((node instanceof ASTExistsExpression)) {
      return Utils.capitalize(((ASTExistsExpression) node).getInDeclaration(0).
        getInDeclarationVariable(0).getName());
    }
    return "";
  }

  /**
   * Generates a Prolog query from an OCL expression
   * @param e An AST of an OCL expression
   * @param compName The name of the component for which the expression should be generated
   */
  public static String generateOCLQuery(ASTExpression e, String compName) {
    GeneratorSetup setup = new GeneratorSetup();
    // Prolog Comment
    setup.setCommentStart("%");
    setup.setCommentEnd("");

    GeneratorEngine engine = new GeneratorEngine(setup);
    return engine.generate("templates/oclquery.ftl", e, compName).toString();
  }
}

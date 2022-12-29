/* (c) https://github.com/MontiCore/monticore */
package montithings.services.prolog_generator.devicedescription.generator;

import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import montithings.services.prolog_generator.devicedescription._visitor.DeviceDescriptionTraverser;
import montithings.services.prolog_generator.devicedescription._visitor.DeviceDescriptionTraverserImplementation;

public class ObjectDiagramToPrologConverter {

  private ODBasisToPrologPrettyPrinter oDBasisToPrologPrettyPrinter;
  private ODAttributeToPrologPrettyPrinter oDAttributeToPrologPrettyPrinter;
  private MCBasicTypesPrettyPrinter mCBasicTypesPrettyPrinter;
  private CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter;
  private ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter;
  private MCBasicsPrettyPrinter mCBasicsPrettyPrinter;
  private MCCommonLiteralsPrettyPrinter mCCommonLiteralsPrettyPrinter;

  private IndentPrinter printer;
  private DeviceDescriptionTraverser traverser;

  public ObjectDiagramToPrologConverter() {
    printer = new IndentPrinter();
    traverser = new DeviceDescriptionTraverserImplementation();

    oDBasisToPrologPrettyPrinter = new ODBasisToPrologPrettyPrinter(printer);
    traverser.setODBasisHandler(oDBasisToPrologPrettyPrinter);

    oDAttributeToPrologPrettyPrinter = new ODAttributeToPrologPrettyPrinter(printer);
    traverser.setODAttributeHandler(oDAttributeToPrologPrettyPrinter);

    mCBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mCBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mCBasicTypesPrettyPrinter);

    commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);

    expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);

    mCBasicsPrettyPrinter = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(mCBasicsPrettyPrinter);

    mCCommonLiteralsPrettyPrinter = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(mCCommonLiteralsPrettyPrinter);
    traverser.add4MCCommonLiterals(mCCommonLiteralsPrettyPrinter);
  }

  public String printODElement(ASTODElement node) {
    printer.clearBuffer();
    node.accept(traverser);
    return printer.getContent();
  }

  /**
   * Generates a Prolog file containing facts for devices
   * @param devices An AST based on an object diagram file
   */
  public static String generateFacts(ASTObjectDiagram devices) {
    GeneratorSetup setup = new GeneratorSetup();
    // Prolog Comment
    setup.setCommentStart("%");
    setup.setCommentEnd("");

    GeneratorEngine engine = new GeneratorEngine(setup);
    return engine.generate("templates/devicedescription.ftl", devices).toString();

  }
}

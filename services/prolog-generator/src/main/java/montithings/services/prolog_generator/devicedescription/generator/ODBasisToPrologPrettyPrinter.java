package montithings.services.prolog_generator.devicedescription.generator;

import de.monticore.odbasis._ast.*;
import de.monticore.odbasis.prettyprinter.ODBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import montithings.services.prolog_generator.Utils;

public class ODBasisToPrologPrettyPrinter extends ODBasisPrettyPrinter {

  public ODBasisToPrologPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  private String hardwareName;

  @Override
  public void handle(ASTObjectDiagram node) {
    if (node.isPresentStereotype()) {
      Log.error("Stereotype not allowed in object diagrams");
    }
    for (ASTODElement element : node.getODElementList()) {
      element.accept(getTraverser());
      getPrinter().println(".");
    }
  }

  @Override
  public void handle(ASTODNamedObject node) {
    //ignore modifier
    hardwareName = node.getMCObjectType().printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter()));
    hardwareName = Utils.toFirstLower(hardwareName);
    //print type
    getPrinter().println("type(" + node.getName() + ", " + hardwareName + ").");
    for (ASTODAttribute attribute : node.getODAttributeList()) {
      attribute.accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTODAnonymousObject node) {
    //ignore modifier
    hardwareName = node.getMCObjectType().printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter()));
    hardwareName = Utils.toFirstLower(hardwareName);
    for (ASTODAttribute attribute : node.getODAttributeList()) {
      attribute.accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTODAttribute node) {
    getPrinter().print(node.getName() + "(" + hardwareName + ", ");
    if (!node.isPresentODValue()) {
      Log.error("A value has to be present for every attribute in the object diagrams used by the prolog generator");
    }
    node.getODValue().accept(getTraverser());
    getPrinter().println(").");
  }

  @Override
  public void handle(ASTODAbsent node) {
    Log.error("Absent Values are currently not supported by the prolog generator");
  }

  @Override
  public void handle(ASTODSimpleAttributeValue node) {
    node.getExpression().accept(getTraverser());
  }

  @Override
  public void handle(ASTODName node) {
    getPrinter().print(node.getName());
  }
}

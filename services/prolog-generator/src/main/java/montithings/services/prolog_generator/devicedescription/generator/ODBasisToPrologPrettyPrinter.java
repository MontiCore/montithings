package montithings.services.prolog_generator.devicedescription.generator;

import de.monticore.odbasis._ast.*;
import de.monticore.odbasis.prettyprinter.ODBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import montithings.services.prolog_generator.Utils;

public class ODBasisToPrologPrettyPrinter extends ODBasisPrettyPrinter {

  private String hardwareName;

  private String currentObjectName;

  public ODBasisToPrologPrettyPrinter(IndentPrinter printer) {
    super(printer);
    hardwareName = "";
    currentObjectName= "";
  }

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
    //check to see if current object is outermost object
    outerObjectCheck(node.getMCObjectType());
    //print type
    getPrinter().println("type(" + node.getName() + ", " + hardwareName + ").");
    for (ASTODAttribute attribute : node.getODAttributeList()) {
      attribute.accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTODAnonymousObject node) {
    //check to see if current object is outermost object
    outerObjectCheck(node.getMCObjectType());
    for (ASTODAttribute attribute : node.getODAttributeList()) {
      attribute.accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTODAttribute node) {
    if (!node.isPresentODValue()) {
      Log.error("A value has to be present for every attribute in the object diagrams used by the prolog generator");
    }

    //skip if attribute is assigned to object
    if (node.getODValue() instanceof ASTODObject){
      node.getODValue().accept(getTraverser());
      return;
    }

    String name = "";
    //attach prefix for attributes in inner objects
    if (currentObjectName.length() > 0) {
      name = currentObjectName + "__";
    }
    name += node.getName();
    getPrinter().print(name + "(" + hardwareName + ", ");
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

  private void outerObjectCheck(ASTMCObjectType mcObjectType) {
    if (hardwareName.length() == 0) {
      hardwareName = mcObjectType.printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter()));
      hardwareName = Utils.toFirstLower(hardwareName);
    }
    else {
      currentObjectName = mcObjectType.printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter()));
      currentObjectName = Utils.toFirstLower(currentObjectName);
    }
  }
}

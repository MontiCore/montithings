// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;
import prepostcondition._visitor.PrePostConditionPrettyPrinter;

public class PrePostConditionToMontiArcPrettyPrinter extends PrePostConditionPrettyPrinter {
  @Override
  public void handle(ASTPrecondition node) {
    //intentionally left empty - not covered by MontiArc
  }
  
  @Override
  public void handle(ASTPostcondition node) {
    //intentionally left empty - not covered by MontiArc
  }
}
// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import behavior._ast.ASTAgoQualification;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import montithings._visitor.MontiThingsVisitor;

import java.util.HashMap;
import java.util.Map;

public class FindAgoQualificationsVisitor implements MontiThingsVisitor {
  protected Map<String, Double> agoQualifications = new HashMap<>();

  @Override
  public void visit(ASTAgoQualification node) {
    Preconditions.checkArgument(node != null);
    if(node.getExpression() instanceof ASTNameExpression){
      String name = ((ASTNameExpression) node.getExpression()).getName();
      if(agoQualifications.containsKey(name)){
        if(SIUnitLiteralDecoder.valueOf(node.getSIUnitLiteral()) > agoQualifications.get(name)){
          agoQualifications.replace(name, SIUnitLiteralDecoder.valueOf(node.getSIUnitLiteral()));
        }
      }
      else {
        agoQualifications.put(name, SIUnitLiteralDecoder.valueOf(node.getSIUnitLiteral()));
      }
    }
  }

  public Map<String, Double> getAgoQualifications() {
    return agoQualifications;
  }
}


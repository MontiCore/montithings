// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import behavior._ast.ASTAgoQualification;
import behavior._visitor.BehaviorHandler;
import behavior._visitor.BehaviorTraverser;
import behavior._visitor.BehaviorVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;

import java.util.HashMap;
import java.util.Map;

public class FindAgoQualificationsVisitor
  implements BehaviorVisitor2, BehaviorHandler {

  protected BehaviorTraverser traverser;

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

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4Behavior(this);
    traverser.setBehaviorHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Map<String, Double> getAgoQualifications() {
    return agoQualifications;
  }

  @Override public BehaviorTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(BehaviorTraverser traverser) {
    this.traverser = traverser;
  }
}


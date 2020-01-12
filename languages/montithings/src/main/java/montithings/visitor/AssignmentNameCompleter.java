// (c) https://github.com/MontiCore/monticore
package montithings.visitor;

import de.monticore.symboltable.Scope;
import montithings._visitor.MontiThingsVisitor;

/**
 * Only necessary since the wrong visitor is used if this class is not present
 *
 * @author  Joshua Fürste
 */
class AssignmentNameCompleter extends montiarc.visitor.AssignmentNameCompleter implements MontiThingsVisitor {

  public AssignmentNameCompleter(Scope automatonScope) {
    super(automatonScope);
  }
}


/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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


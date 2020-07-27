// (c) https://github.com/MontiCore/monticore
package montithings.generator.data;

import montiarc._ast.ASTArcInstant;
import montiarc._ast.ASTArcSync;
import montiarc._ast.ASTArcTimeMode;
import montiarc._ast.ASTArcUntimed;

/**
 * Time modes of components as definded by MontiArc
 */
public enum ComponentTiming {
  INSTANT,
  SYNC,
  UNTIMED;

  ComponentTiming from(ASTArcTimeMode timeMode) {
    if (timeMode instanceof ASTArcInstant) return INSTANT;
    if (timeMode instanceof ASTArcSync) return SYNC;
    if (timeMode instanceof ASTArcUntimed) return UNTIMED;
    return INSTANT;
  }
}

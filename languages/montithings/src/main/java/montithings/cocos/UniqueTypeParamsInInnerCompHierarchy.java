/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montithings.visitor.TypeUniquenessVisitor;

/**
 * TODO
 *
 * @author (last commit)
 */

public class UniqueTypeParamsInInnerCompHierarchy
        implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    new TypeUniquenessVisitor().handle(node);
  }
}

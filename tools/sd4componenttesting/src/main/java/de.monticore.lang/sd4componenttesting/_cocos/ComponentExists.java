package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CObjectPort;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ComponentExists implements SD4ComponentTestingASTSD4CObjectPortCoCo {

  @Override
  public void check(ASTSD4CObjectPort node) {
    String implComp = node.getComponent().getQName();

    System.out.println("jkhjkhrjvhrjvhr vrh jk hjhkrcjkrhr cjkh kjh kjher kjrjkrh ");
    System.out.println(implComp);
    System.out.println("jkhjkhrjvhrjvhr vrh jk hjhkrcjkrhr cjkh kjh kjher kjrjkrh ");

    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(implComp);

    System.out.println("jkhjkhrjvhrjvhr vrh jk hjhkrcjkrhr cjkh kjh kjher kjrjkrh ");
    System.out.println(comp.toString());
    System.out.println("jkhjkhrjvhrjvhr vrh jk hjhkrcjkrhr cjkh kjh kjher kjrjkrh ");

    if (!comp.isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
    }
  }
}

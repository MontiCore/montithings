// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import montithings.MontiThingsMill;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class ResetMTMill extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    MontiThingsMill.reset();
    MontiThingsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

}

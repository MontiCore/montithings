package cd4montithings._symboltable;

import cd4montithings._visitor.CD4MontiThingsHandler;
import cd4montithings._visitor.CD4MontiThingsTraverser;
import de.monticore.symboltable.serialization.JsonPrinter;

public class CD4MontiThingsSymbols2Json extends CD4MontiThingsSymbols2JsonTOP implements CD4MontiThingsHandler {
  public CD4MontiThingsSymbols2Json() {
    getTraverser().setCD4MontiThingsHandler(this);
  }

  public CD4MontiThingsSymbols2Json(CD4MontiThingsTraverser traverser, JsonPrinter printer) {
    super(traverser, printer);
    getTraverser().setCD4MontiThingsHandler(this);
  }

  @Override
  public void traverse(ICD4MontiThingsScope node) {
    CD4MontiThingsHandler.super.traverse(node);

    for (de.monticore.cdbasis._symboltable.CDPackageSymbol s : node.getLocalCDPackageSymbols()) {
      getTraverser().traverse((ICD4MontiThingsScope) s.getSpannedScope());
    }
  }

}

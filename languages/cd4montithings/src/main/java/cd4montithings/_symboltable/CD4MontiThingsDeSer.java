package cd4montithings._symboltable;

import de.monticore.cdbasis._symboltable.CDBasisDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;

import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.deserializeFurtherObjects;
import static de.monticore.cdassociation._symboltable.CDAssociationDeSer.serializeFurtherObjects;

public class CD4MontiThingsDeSer extends CD4MontiThingsDeSerTOP{
  @Override
  protected void deserializeSymbols(ICD4MontiThingsScope scope, JsonObject scopeJson) {
    super.deserializeSymbols(scope, scopeJson);
    CDBasisDeSer.moveCDTypeSymbolsToPackage(scope);
  }

  @Override
  public void serializeAddons(ICD4MontiThingsArtifactScope toSerialize, CD4MontiThingsSymbols2Json s2j) {
    super.serializeAddons(toSerialize, s2j);
    serializeFurtherObjects(s2j.printer);
  }

  @Override
  public void deserializeAddons(ICD4MontiThingsArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(scopeJson);
  }

}

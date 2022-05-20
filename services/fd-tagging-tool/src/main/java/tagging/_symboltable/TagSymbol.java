// (c) https://github.com/MontiCore/monticore
package tagging._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagSymbol extends TagSymbolTOP {
  protected Optional<FeatureSymbol> featureSymbol;

  protected List<Optional<ComponentInstanceSymbol>> componentSymbolList;

  public TagSymbol(String name) {
    super(name);
    componentSymbolList = new ArrayList<>();
  }

  public Optional<FeatureSymbol> getFeatureSymbol() {
    return this.featureSymbol;
  }

  public void setFeatureSymbol(Optional<FeatureSymbol> f) {
    this.featureSymbol = f;
  }

  public List<Optional<ComponentInstanceSymbol>> getComponentSymbolList() {
    return this.componentSymbolList;
  }

  public Optional<ComponentInstanceSymbol> getComponentSymbolListIndex(int i) {
    return this.componentSymbolList.get(i);
  }

  public void setComponentSymbolList(List<Optional<ComponentInstanceSymbol>> l) {
    this.componentSymbolList = l;
  }

  public void setComponentSymbol(int i, Optional<ComponentInstanceSymbol> c) {
    this.componentSymbolList.set(i, c);
  }
}

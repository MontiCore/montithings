package tagging._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;

import java.util.Optional;

public class TaggingSymbol extends TaggingSymbolTOP {
  protected Optional<FeatureDiagramSymbol> featureDiagramSymbol;

  protected Optional<ComponentTypeSymbol> mainComponentSymbol;

  public TaggingSymbol(String name) {
    super(name);
  }

  public Optional<FeatureDiagramSymbol> getFeatureDiagramSymbol() {
    return this.featureDiagramSymbol;
  }

  public void setFeatureDiagramSymbol(Optional<FeatureDiagramSymbol> f) {
    this.featureDiagramSymbol = f;
  }

  public Optional<ComponentTypeSymbol> getMainComponentSymbol() {
    return this.mainComponentSymbol;
  }

  public void setMainComponentSymbol(Optional<ComponentTypeSymbol> c) {
    this.mainComponentSymbol = c;
  }
}

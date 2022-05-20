// (c) https://github.com/MontiCore/monticore
package tagging._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsGlobalScope;
import tagging._ast.ASTTag;
import tagging._ast.ASTTagging;

import java.util.List;
import java.util.Optional;

public class TaggingScopesGenitor extends TaggingScopesGenitorTOP {
  @Override
  public ITaggingArtifactScope createFromAST(ASTTagging rootNode) {
    ITaggingArtifactScope artifactScope = super.createFromAST(rootNode);
    rootNode.initAllComponents();
    rootNode.initAllFeatures();
    artifactScope.setAstNode(rootNode);
    artifactScope.setName(rootNode.getName());
    artifactScope.setPackageName(rootNode.getPackage().getQName());

    //call global scopes of the languages
    IFeatureDiagramGlobalScope fdGS = FeatureDiagramMill.globalScope();
    IMontiThingsGlobalScope mtGS = MontiThingsMill.globalScope();

    //get qualified name of tagging
    String qTaggingName = rootNode.getPackage().getQName() + "." + rootNode.getName();

    //Resolve the feature diagram to check if it even exists.
    Optional<FeatureDiagramSymbol> featureDiagram;
    featureDiagram = fdGS.resolveFeatureDiagram(qTaggingName);
    rootNode.getSymbol().setFeatureDiagramSymbol(featureDiagram);

    //Resolve the main component to check if it even exists.
    Optional<ComponentTypeSymbol> mainComponentSymbol;
    mainComponentSymbol = mtGS.resolveComponentType(qTaggingName);
    rootNode.getSymbol().setMainComponentSymbol(mainComponentSymbol);

    //Resolve features and components
    Optional<ComponentInstanceSymbol> componentInstance;
    Optional<ComponentTypeSymbol> componentType;
    List<String> partsList;
    for (ASTTag tag : rootNode.getTagList()) {
      tag.getSymbol()
        .setFeatureSymbol(fdGS.resolveFeature(qTaggingName + "." + tag.getFeature().getQName()));
      for (ASTMCQualifiedName component : tag.getComponentsList()) {
        partsList = component.getPartsList();
        componentInstance = mtGS.resolveComponentInstance(qTaggingName + "." + partsList.get(0));
        if (componentInstance.isPresent()) {
          componentType = Optional.ofNullable(componentInstance.get().getType());
          for (int i = 1; i <= partsList.size() - 1; i++) {
            componentInstance = componentType.get().getSpannedScope()
              .resolveComponentInstanceDown(partsList.get(i));
            if (!componentInstance.isPresent())
              break;
            componentType = Optional.ofNullable(componentInstance.get().getType());
          }
        }
        tag.getSymbol().getComponentSymbolList().add(componentInstance);
      }
    }
    return artifactScope;
  }
}

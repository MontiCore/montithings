/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sd4componenttesting._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;

import java.util.Optional;

public class SD4ComponentTestingScopesGenitor extends SD4ComponentTestingScopesGenitorTOP {

  @Override
  public ISD4ComponentTestingArtifactScope createFromAST(ASTSD4Artifact rootNode) {
    ISD4ComponentTestingArtifactScope artifactScope = super.createFromAST(rootNode);
    String packageDeclaration = rootNode.isPresentPackageDeclaration() ? rootNode.getPackageDeclaration().getQName() : "";
    artifactScope.setPackageName(packageDeclaration);

    Optional<ComponentTypeSymbol> mainComp = rootNode.getEnclosingScope().resolveComponentType(rootNode.getTestDiagram().getMainComponent());
    mainComp.ifPresent(artifactScope::setMainComponentTypeSymbol);

    return artifactScope;
  }
}

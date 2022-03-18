// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CDelay;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CExpression;
import de.monticore.lang.sd4componenttesting.util.SD4CElementType;

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

  @Override
  public void visit (ASTSD4CConnection node)  {
    super.visit(node);

    if (!node.isPresentSource() && !node.getTargetList().isEmpty()) {
      node.setType(SD4CElementType.MAIN_INPUT);
    } else if (node.isPresentSource() && node.getTargetList().isEmpty()) {
      node.setType(SD4CElementType.MAIN_OUTPUT);
    } else {
      node.setType(SD4CElementType.DEFAULT);
    }
  }

  @Override
  public void visit (ASTSD4CExpression node) {
    super.visit(node);

    node.setType(SD4CElementType.EXPRESSION);
  }

  @Override
  public void visit (ASTSD4CDelay node) {
    super.visit(node);

    node.setType(SD4CElementType.DELAY);
  }
}

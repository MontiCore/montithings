// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import montiarc._ast.ASTMACompilationUnit;

import java.util.Collection;

public interface MontiThingsTrafo {
  Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
    Collection<ASTMACompilationUnit> addedModels,
    ASTMACompilationUnit targetComp) throws Exception;
}

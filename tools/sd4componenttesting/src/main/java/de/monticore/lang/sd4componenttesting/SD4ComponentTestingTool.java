// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting;

import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sd4componenttesting._cocos.SD4ComponentTestingCoCos;
import de.se_rwth.commons.logging.Log;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import de.monticore.lang.sd4componenttesting._parser.SD4ComponentTestingParser;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingGlobalScope;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingScope;
import de.monticore.lang.sd4componenttesting._symboltable.SD4ComponentTestingScopesGenitorDelegator;
import de.monticore.lang.sd4componenttesting._symboltable.adapters.Name2ComponentTypeResolvingDelegate;
import de.monticore.lang.sd4componenttesting._symboltable.adapters.Name2ComponentInstanceResolvingDelegate;
import de.monticore.lang.sd4componenttesting._symboltable.adapters.Name2PortResolvingDelegate;
import de.monticore.lang.sd4componenttesting.generator.SD4ComponentTestingGenerator;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcGlobalScope;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/**
 * Provides useful methods for handling the SD4ComponentTesting language.
 */
public class SD4ComponentTestingTool {
  protected IMontiArcGlobalScope maGlobalScope;

  public ISD4ComponentTestingScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    Name2ComponentInstanceResolvingDelegate componentInstanceResolvingDelegate;
    Name2ComponentTypeResolvingDelegate componentTypeResolvingDelegate;
    Name2PortResolvingDelegate portResolvingDelegate;

    if(this.maGlobalScope == null) {
      MontiArcMill.globalScope().clear();
      MontiArcMill.reset();
      MontiArcMill.init();

      this.maGlobalScope = MontiArcMill.globalScope();
      this.maGlobalScope.setModelPath(mp);

      MontiArcTool tool = new MontiArcTool();
      tool.addBasicTypes();
      tool.processModels(this.maGlobalScope);
    }
    componentInstanceResolvingDelegate = new Name2ComponentInstanceResolvingDelegate(this.maGlobalScope);
    componentTypeResolvingDelegate = new Name2ComponentTypeResolvingDelegate(this.maGlobalScope);
    portResolvingDelegate = new Name2PortResolvingDelegate(this.maGlobalScope);

    SD4ComponentTestingMill.globalScope().clear();
    SD4ComponentTestingMill.reset();
    SD4ComponentTestingMill.init();

    ISD4ComponentTestingGlobalScope sd4ComponentTestingGlobalScope = SD4ComponentTestingMill.globalScope();
    sd4ComponentTestingGlobalScope.setModelPath(mp);
    sd4ComponentTestingGlobalScope.addAdaptedComponentInstanceSymbolResolver(componentInstanceResolvingDelegate);
    sd4ComponentTestingGlobalScope.addAdaptedComponentTypeSymbolResolver(componentTypeResolvingDelegate);
    sd4ComponentTestingGlobalScope.addAdaptedPortSymbolResolver(portResolvingDelegate);

    return sd4ComponentTestingGlobalScope;
  }

  public void createSymbolTableFromAST(ASTSD4Artifact ast) {
    SD4ComponentTestingScopesGenitorDelegator genitor = SD4ComponentTestingMill.scopesGenitorDelegator();
    genitor.createFromAST(ast);
  }

  protected ASTSD4Artifact parseModel(String modelFile) {
    Path model = Paths.get(modelFile);
    SD4ComponentTestingParser parser = new SD4ComponentTestingParser();
    Optional<ASTSD4Artifact> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      return optAutomaton.get();
    }
    catch (Exception e) {
      e.printStackTrace();
      Log.error("There was an exception when parsing the model " + modelFile + ": "
        + e.getMessage());
    }
    return null;
  }

  public ASTSD4Artifact loadModel(String modelPath, String sd4cModelPath) {
    initSymbolTable(new File(modelPath));

    ASTSD4Artifact ast = parseModel(sd4cModelPath);

    if (ast != null) {
      createSymbolTableFromAST(ast);
      SD4ComponentTestingCoCos.createChecker().checkAll(ast);
      return ast;
    }
    return null;
  }

  public Path generate(String modelPath, String sd4cModelFile, String outputFile) {
    ASTSD4Artifact ast = loadModel(modelPath, sd4cModelFile);
    return SD4ComponentTestingGenerator.generate(ast, outputFile);
  }
}

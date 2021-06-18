// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting;

import de.monticore.lang.sd4componenttesting._ast.ASTSDArtifact;
import de.monticore.lang.sd4componenttesting._cocos.SD4ComponentTestingCoCoChecker;
import de.monticore.lang.sd4componenttesting._cocos.SD4ComponentTestingCoCos;
import de.monticore.lang.sd4componenttesting._parser.SD4ComponentTestingParser;
import de.monticore.lang.sd4componenttesting._symboltable.SD4ComponentTestingScopesGenitorDelegator;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingGlobalScope;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingScope;
import de.monticore.lang.sd4componenttesting._symboltable.adapters.MCQualifiedName2ComponentInstanceResolvingDelegate;
import de.monticore.lang.sd4componenttesting._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._symboltable.IMontiArcGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/**
 * Provides useful methods for handling the Bindings language.
 */
public class SD4ComponentTestingTool {

  public static final String FILE_ENDING = "sd4c";

  protected SD4ComponentTestingCoCoChecker checker;

  protected boolean isSymTabInitialized;

  protected IMontiArcGlobalScope maGlobalScope;

  public SD4ComponentTestingTool() {
    this(SD4ComponentTestingCoCos.createChecker());
  }

  public SD4ComponentTestingTool(@NotNull SD4ComponentTestingCoCoChecker checker) {
    Preconditions.checkArgument(checker != null);
    this.checker = checker;
    this.isSymTabInitialized = false;
  }

  public Optional<ASTSDArtifact> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    SD4ComponentTestingParser p = new SD4ComponentTestingParser();
    Optional<ASTSDArtifact> compUnit;
    try {
      compUnit = p.parse(filename);
      return compUnit;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelpath is src/main/resources.
   *
   * @param modelPaths paths of all folders containing models
   * @return The initialized symbol table
   */
  public ISD4ComponentTestingGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate;
    MCQualifiedName2ComponentInstanceResolvingDelegate componentInstanceResolvingDelegate;
    if (this.maGlobalScope == null) {
      this.maGlobalScope = MontiArcMill.globalScope();
      this.maGlobalScope.setModelPath(mp);
      this.maGlobalScope.setFileExt("mt");
      MontiArcTool tool = new MontiArcTool();
      tool.addBasicTypes();
      tool.processModels(this.maGlobalScope);
    }
    componentTypeResolvingDelegate =
      new MCQualifiedName2ComponentTypeResolvingDelegate(this.maGlobalScope);
    componentInstanceResolvingDelegate =
      new MCQualifiedName2ComponentInstanceResolvingDelegate(this.maGlobalScope);

    ISD4ComponentTestingGlobalScope componentTestingGlobalScope = SD4ComponentTestingMill.globalScope();
    componentTestingGlobalScope.setModelPath(mp);
    componentTestingGlobalScope.setFileExt("sd4c");
    componentTestingGlobalScope.addAdaptedComponentTypeSymbolResolver(componentTypeResolvingDelegate);
    componentTestingGlobalScope.addAdaptedComponentInstanceSymbolResolver(componentInstanceResolvingDelegate);

    isSymTabInitialized = true;
    return componentTestingGlobalScope;
  }

  /**
   * Creates a GlobalScope from a given model path and adds the given AST to it.
   *
   * @param ast        node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public ISD4ComponentTestingGlobalScope createSymboltable(ASTSDArtifact ast,
                                                File... modelPaths) {

    ISD4ComponentTestingGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast, globalScope);
  }

  /**
   * Creates the symbol table for a given AST and adds it to the given global scope.
   *
   * @param ast         node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return extended global scope
   */
  public ISD4ComponentTestingGlobalScope createSymboltable(ASTSDArtifact ast,
                                                ISD4ComponentTestingGlobalScope globalScope) {

    SD4ComponentTestingScopesGenitorDelegator stc = new SD4ComponentTestingScopesGenitorDelegator(globalScope);
    stc.createFromAST(ast);

    return globalScope;
  }

  /**
   * Initializes the Symboltable by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelPath is src/main/resources.
   *
   * @param modelPath The model path for the symbol table
   * @return the initialized symbol table
   */
  public ISD4ComponentTestingScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }

  public IMontiArcGlobalScope getMaGlobalScope() {
    return maGlobalScope;
  }

  /**
   * Setter for the global scope that should be used for resolving non native symbols.
   *
   * @param maGlobalScope globalScope used for resolving non native symbols
   */
  public void setMaGlobalScope(IMontiArcGlobalScope maGlobalScope) {
    this.maGlobalScope = maGlobalScope;
  }
}

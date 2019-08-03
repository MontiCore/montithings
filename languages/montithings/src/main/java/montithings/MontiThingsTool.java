/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings;

import com.google.common.collect.Sets;
import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.JavaDefaultTypesManager;
import montithings._cocos.MontiThingsCoCoChecker;
import montithings._parser.MontiThingsParser;
import montithings._symboltable.MontiThingsLanguageFamily;
import montithings.cocos.MontiThingsCoCos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class MontiThingsTool {

  private static final String[] primitiveTypes = { "bool", "std::byte", "char", "double", "float",
          "int", "long", "short", "null" , "char16_t" , "char32_t", "wchar_t", "std::string",
          "int8_t", "int16_t", "int32_t", "int64_t", "uint8_t", "uint16_t", "uint32_t",
          "uint64_t"};

  protected ModelingLanguageFamily family;

  private MontiThingsCoCoChecker checker;

  private boolean isSymTabInitialized;

  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiThingsTool() {
    this(new MontiThingsLanguageFamily(), MontiThingsCoCos.createChecker());
  }
  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiThingsTool(ModelingLanguageFamily fam, MontiThingsCoCoChecker checker) {
    this.family = fam;
    this.checker = checker;
    isSymTabInitialized = false;
  }

  /**
   * Constructor for montiarc.MontiArcTool
   */
  public MontiThingsTool(ModelingLanguageFamily fam) {
    this(fam, MontiThingsCoCos.createChecker());
  }

  public Optional<ASTMACompilationUnit> parse(String filename) {
    MontiThingsParser p = new MontiThingsParser();
    Optional<ASTMACompilationUnit> compUnit;
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
   * Executes CoCos on MontiArcNode
   *
   * @param node
   * @return true if no errors occurred
   */
  public boolean checkCoCos(ASTMontiArcNode node) {
    if (!isSymTabInitialized) {
      Log.error("Symtab has to be initialized before checking CoCos");
      return false;
    }
    if (!node.getSymbolOpt().isPresent() && !node.getSpannedScopeOpt().isPresent()) {
      Log.error(
              "Symtab is not linked with passed node! Call getSymbol() or getASTNode() for getting the ast.");
    }

    checker.checkAll(node);
    if (Log.getErrorCount() != 0) {
      Log.debug("Found " + Log.getErrorCount() + " errors in node " + node + ".", "XX");
      return false;
    }
    return true;
  }

  /**
   * Loads a ComponentSymbol with the passed componentName. The componentName is the full qualified
   * name of the component model. Modelpaths are folders relative to the project path and containing
   * the packages the models are located in. When the ComponentSymbol is resolvable it is returned.
   * Otherwise the optional is empty.
   *
   * @param componentName Name of the component
   * @param modelPaths Folders containing the packages with models
   * @return
   */
  public Optional<ComponentSymbol> loadComponentSymbolWithoutCocos(String componentName,
                                                                   File... modelPaths) {
    Scope s = initSymbolTable(modelPaths);
    return s.<ComponentSymbol> resolve(componentName, ComponentSymbol.KIND);
  }

  public Optional<ComponentSymbol> loadComponentSymbolWithCocos(String componentName,
                                                                File... modelPaths) {
    Optional<ComponentSymbol> compSym = loadComponentSymbolWithoutCocos(componentName, modelPaths);

    if (compSym.isPresent()) {
      checkCoCos((ASTMontiArcNode) compSym.get().getAstNode().get());
    }

    return compSym;
  }

  /**
   * Initializes the symbol table by introducing scopes for the passed modelpaths. It does not create
   * the symbol table! Symbols for models within the modelpaths are not added to the symboltable
   * until resolve() is called. Modelpaths are relative to the project path and do contain all the
   * packages the models are located in. E.g. if model with fqn a.b.C lies in folder
   * src/main/resources/models/a/b/C.arc, the modelpath is src/main/resources.
   *
   * @param modelPaths
   * @return The initialized symbol table
   */
  public Scope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    GlobalScope gs = new GlobalScope(mp, family);
    addCPPPrimitiveTypes(gs);
    isSymTabInitialized = true;
    return gs;
  }

  public Scope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }




  public static void addCPPPrimitiveTypes(GlobalScope globalScope) {
    for (String primType : primitiveTypes) {
      JavaTypeSymbol jTypeSymbol = new JavaSymbolFactory().createClassSymbol(primType);
      ArtifactScope spannedScope = new ArtifactScope("java.lang", new ArrayList<ImportStatement>());
      spannedScope.setResolvingFilters(globalScope.getResolvingFilters());
      spannedScope.setEnclosingScope(globalScope);
      spannedScope.add(jTypeSymbol);
      globalScope.addSubScope(spannedScope);
    }
  }

}

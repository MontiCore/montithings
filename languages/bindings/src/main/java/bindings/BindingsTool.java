// (c) https://github.com/MontiCore/monticore
package bindings;

import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.BindingsCoCos;
import bindings._parser.BindingsParser;
import bindings._symboltable.BindingsGlobalScope;
import bindings._symboltable.BindingsLanguage;
import bindings._symboltable.BindingsSymbolTableCreatorDelegator;
import bindings._symboltable.IBindingsScope;
import bindings._symboltable.adapters.MCQualifiedName2ComponentTypeResolvingDelegate;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import montithings.MontiThingsMill;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings._symboltable.MontiThingsLanguage;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class BindingsTool {

  protected BindingsLanguage language;

  protected BindingsCoCoChecker checker;

  protected boolean isSymTabInitialized;

  public BindingsTool() {
    this(BindingsCoCos.createChecker(), new BindingsLanguage());
  }

  public BindingsTool(@NotNull BindingsCoCoChecker checker, @NotNull BindingsLanguage language) {
    Preconditions.checkArgument(checker != null);
    Preconditions.checkArgument(language != null);
    this.checker = checker;
    this.language = language;
    this.isSymTabInitialized = false;
  }

  public Optional<ASTBindingsCompilationUnit> parse(@NotNull String filename) {
    Preconditions.checkArgument(filename != null);
    BindingsParser p = new BindingsParser();
    Optional<ASTBindingsCompilationUnit> compUnit;
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
  public BindingsGlobalScope initSymbolTable(File... modelPaths) {
    Set<Path> p = Sets.newHashSet();
    for (File mP : modelPaths) {
      p.add(Paths.get(mP.getAbsolutePath()));
    }

    final ModelPath mp = new ModelPath(p);

    MontiThingsLanguage mtLang = MontiThingsMill.montiThingsLanguageBuilder().build();
    MontiThingsGlobalScope mtGlobalScope = MontiThingsMill.montiThingsGlobalScopeBuilder()
        .setModelPath(mp)
        .setMontiThingsLanguage(mtLang)
        .build();

    MCQualifiedName2ComponentTypeResolvingDelegate componentTypeResolvingDelegate =
        new MCQualifiedName2ComponentTypeResolvingDelegate(mtGlobalScope);

    BindingsGlobalScope bindingsGlobalScope = new BindingsGlobalScope(mp, language);
    bindingsGlobalScope.addAdaptedComponentTypeSymbolResolvingDelegate(componentTypeResolvingDelegate);

    isSymTabInitialized = true;
    return bindingsGlobalScope;
  }

  /**
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param modelPaths path that contains all models
   * @return created global scope
   */
  public BindingsGlobalScope createSymboltable(ASTBindingsCompilationUnit ast,
      File... modelPaths) {

    BindingsGlobalScope globalScope = initSymbolTable(modelPaths);

    return createSymboltable(ast,globalScope);
  }

  /**
   * Creates a GlobalScope that uses CDLangExtension AST and a given model path.
   *
   * @param ast node used to create symboltable
   * @param globalScope globalScope used for the symbolTable
   * @return created global scope
   */
  public BindingsGlobalScope createSymboltable(ASTBindingsCompilationUnit ast,
      BindingsGlobalScope globalScope) {

    BindingsSymbolTableCreatorDelegator stc = language
        .getSymbolTableCreator(globalScope);
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
  public IBindingsScope initSymbolTable(String modelPath) {
    return initSymbolTable(Paths.get(modelPath).toFile());
  }
  
}

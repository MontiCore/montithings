package montithings.generator.cd2proto;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import montithings.generator.cd2cpp.AssociationHelper;
import montithings.generator.cd2proto.helper.TypeHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate Protocol Buffer files (.proto) from class diagrams.
 */
public class ProtoGenerator {

  /**
   * The directory where to put the generated files.
   */
  final private Path outputDir;

  /**
   * The directory with the models to parse.
   */
  final private Path modelPath;

  /**
   * The name of the class diagram to parse.
   */
  final private String modelName;

  /**
   *  The parsed model.
   */
  private ASTCDCompilationUnit compilationUnit;

  /**
   * List of all parsed classes and enums in the class diagram.
   */
  private List<CDTypeSymbol> cdTypeSymbols;

  /**
   * @param outDir directory to put the Protocol Buffer files into
   * @param modelPath directory where to find the model
   * @param modelName name of the model to parse
   */
  public ProtoGenerator(Path outDir, Path modelPath, String modelName) {
    this.outputDir = outDir;
    this.modelPath = modelPath;
    this.modelName = modelName;

    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().setModelPath(new ModelPath(modelPath));

    ((CD4CodeGlobalScope) CD4CodeMill.globalScope()).addBuiltInTypes();
    registerObjectType("String", "Integer", "Double", "Float");
  }

  private void registerObjectType(String... names) {
    for(String name : names) {
      CD4CodeMill.globalScope().add(CD4CodeMill.typeSymbolBuilder()
              .setName(name)
              .setFullName(name)
              .setEnclosingScope(CD4CodeMill.globalScope())
              .setSpannedScope(CD4CodeMill.scope())
              .build());
    }
  }

  private void parse() throws IOException {
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit = CD4CodeMill.parser().parse(modelPath.toFile().getPath() + "/" + modelName.replace(".", File.separator) + ".cd");

    if (!astcdCompilationUnit.isPresent()) {
      // FIXME: What should we do here?
      throw new RuntimeException("Whoot?");
    }
    this.compilationUnit = astcdCompilationUnit.get();

    final ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(compilationUnit);
    this.compilationUnit.accept(new CD4CodeSymbolTableCompleter(compilationUnit).getTraverser());

    cdTypeSymbols = new ArrayList<>((scope.getCDTypeSymbols().values()));

    scope
        .getSubScopes()
        .stream()
        .map(ICDBasisScope::getCDTypeSymbols)
        .collect(Collectors.toList())
        .stream()
        .map(LinkedListMultimap::values)
        .forEach(l -> cdTypeSymbols.addAll(l));
  }

  /**
   * Generate the .proto files.
   *
   * @return set of paths containing each generated file
   * @throws IOException thrown if a file could not be parsed
   */
  public Set<Path> generate() throws IOException {
    parse();

    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(this.outputDir.toFile());
    GeneratorEngine engine = new GeneratorEngine(setup);
    String outFilename = modelName.replace('.', File.separatorChar) + ".proto";
    Path outfile = Paths.get(outFilename);

    // Mimic the package name creation from cd2cpp
    String package_name = compilationUnit.getEnclosingScope().getRealPackageName();
    if (package_name.isEmpty()) {
      package_name = compilationUnit.getEnclosingScope().getName();
    }
    String _package = "montithings." + package_name + ".protobuf";

    engine.generate("templates/protobuf.ftl", outfile, this.compilationUnit, this.cdTypeSymbols, new TypeHelper(), _package, new AssociationHelper());
    return Collections.singleton(setup.getOutputDirectory().toPath().resolve(outfile));
  }
}

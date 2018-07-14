/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package components;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTClassDeclaration;
import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import generation.ComponentElementsCollector;
import generation.GeneratedComponentClassVisitor;
import infrastructure.AbstractGeneratorTest;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-04-27
 * @since TODO
 */
public class ComponentGenerationTest extends AbstractGeneratorTest {

  public static final String PACKAGE = "components";
  public static final String JAVA_FILE_ENDING = ".java";

  @Test
  /**
   * Test whether all generated classes of the model are syntactically and
   * semantically correct.
   * This is done by proceeding through the following steps:
   * 1. Run the generator (Done implicitely through setUp method)
   * 2. Specify the model to check and load the component symbol
   * 3. Determine which files have to be checked for the specified model
   * 4. Assert that all expected files are present
   * 5. Compile the files to ensure syntactic correctness
   * 6. Initialise the JavaDSL global scope
   * 7. Run a visitor that collects the information about expected elements
   *    in the generated files
   * 8. Run the visitors on all generated files and check that exactly the
   *    expected elements are present
   */
  public void testExample() {
    // 2. Specify the model to check
//    final String componentName = "ComponentWithEmptyComponent";
//    final String componentName = "EmptyComponent";
    final String componentName = "AtomicCompWithoutImpl";
    executeGeneratorTest(componentName);
  }

  private void executeGeneratorTest(String componentName) {
    final String qualifiedName = PACKAGE + "." + componentName;

    // Load component symbol
    final ComponentSymbol symbol = generatorTool.loadComponentSymbolWithCocos(qualifiedName, Paths.get(MODEL_PATH).toFile()).orElse(null);
    assertNotNull(symbol);

    // 3. Determine all files which have to be checked
    List<File> filesToCheck = determineFilesToCheck(
        componentName,
        qualifiedName,
        symbol, TARGET_GENERATED_TEST_SOURCES_DIR);

    // 4. Determine if all files are present
    for (File file : filesToCheck) {
      assertTrue("Could not find expected generated file " + file.toString(),
          file.exists());
    }

    // 5. Invoke Java compiler to see whether they are compiling
    assertTrue(AbstractGeneratorTest.isCompiling(filesToCheck));

    // Parse the files with the JavaDSL
    GlobalScope gs = initJavaDSLSymbolTable();

    Optional<Symbol> optinalClassTypeSymbol;
    ASTClassDeclaration javaDSLNode;

    // Collect information about expected features per file
    ASTComponent compUnit = (ASTComponent) symbol.getAstNode().get();
    ComponentElementsCollector compCollector
        = new ComponentElementsCollector(symbol, componentName);
    compCollector.handle(compUnit);

    // Check if all expected elements are present and no other errors occured
    Log.getFindings().clear();

    // Component class
    runVisitorOnFile(gs, compCollector.getClassVisitor(),
        qualifiedName);

    // Input class
    runVisitorOnFile(gs, compCollector.getInputVisitor(),
        qualifiedName + "Input");

    // Result
    runVisitorOnFile(gs, compCollector.getResultVisitor(),
        qualifiedName + "Result");

    // Impl
    // Only run if the component is not composed
    if(symbol.isAtomic()) {
      runVisitorOnFile(gs, compCollector.getImplVisitor(),
          qualifiedName + "Impl");
    }
    // Log checking
    Log.debug("Number of errors found: " + Log.getFindings().size(), "ComponentGenerationTest");
//    assertEquals(0, Log.getFindings().size());
  }

  @Test
  public void testComponentWithEmptyComponent() {
    executeGeneratorTest("ComponentWithEmptyComponent");
  }

  /**
   * Run the visitor with the expected elements on the specified class.
   * @param javaGlobalScope Scope in which the class is present
   * @param visitor The visitor which should be run on the class
   * @param className Name of the class to run the visitor on.
   */
  private void runVisitorOnFile(Scope javaGlobalScope,
                                GeneratedComponentClassVisitor visitor,
                                String className){

    Preconditions.checkNotNull(javaGlobalScope);
    Preconditions.checkNotNull(visitor);
    Preconditions.checkNotNull(className);

    final Symbol optinalClassTypeSymbol
        = javaGlobalScope.resolve(className, JavaTypeSymbol.KIND).orElse(null);

    assertNotNull(optinalClassTypeSymbol);
    assertTrue(optinalClassTypeSymbol.getAstNode().isPresent());
    visitor.handle((ASTClassDeclaration) optinalClassTypeSymbol.getAstNode().get());
    visitor.allExpectedPresent();
  }

  /**
   * Recursively determines a list of File objects which correspond to the
   * locations of the generated files of the component given by the parameters.
   *
   * @param componentName Name of the component
   * @param qualifiedName Qualified name of the component (package.componentName)
   * @param component ComponentSymbol of the Component
   * @param basedir Location of the generated files
   * @return A list of generated files for the specified component
   */
  private List<File> determineFilesToCheck(String componentName, String qualifiedName, ComponentSymbol component, String basedir) {
    List<File> filesToCheck = new ArrayList<>();

    final Optional<String> deploy = component.getStereotype().get("deploy");

    // Add the special deployment file
    if (deploy != null && deploy.isPresent()) {
      filesToCheck.add(
          new File(TARGET_GENERATED_TEST_SOURCES_DIR + PACKAGE + "\\" +
                       "Deploy" + componentName + JAVA_FILE_ENDING));
    }

    if (component.getSubComponents().isEmpty()) {

      // Determine if an automaton or a compute block is present
//      final ASTComponent astNode
//          = (ASTComponent) component.getAstNode().get();

//      for (ASTElement element : astNode.getBody().getElements()) {
//        if (element instanceof ASTBehaviorElement) {
//          final String qualifiedFileName
//              = basedir + qualifiedName.replace('.', '\\') + "Impl";
//          filesToCheck.add(
//              new File(qualifiedFileName + JAVA_FILE_ENDING));
//          break;
//        }
//      }

      final String qualifiedFileName
          = basedir + qualifiedName.replace('.', '\\') + "Impl";
      filesToCheck.add(new File(qualifiedFileName + JAVA_FILE_ENDING));
    } else {

      //Recursively add files for subcomponents
      for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
        final ComponentSymbol referencedSymbol
            = instanceSymbol.getComponentType().getReferencedSymbol();
        filesToCheck.addAll(
            determineFilesToCheck(
                referencedSymbol.getName(),
                referencedSymbol.getFullName(),
                referencedSymbol, basedir));
      }

    }

    // TODO Add super components

    for (String suffix : AbstractGeneratorTest.fileSuffixes) {
      final String qualifiedFileName
          = basedir + qualifiedName.replace('.', '\\') + suffix;
      filesToCheck.add(
          new File(qualifiedFileName + JAVA_FILE_ENDING));
    }

    return filesToCheck;
  }

}

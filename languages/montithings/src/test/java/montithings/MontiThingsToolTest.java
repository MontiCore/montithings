package montithings;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.MontiArcLanguage;
import montiarc.util.DirectoryUtil;
import montiarc.util.Modelfinder;
import montithings._symboltable.IMontiThingsScope;
import montithings._symboltable.MontiThingsLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author kirchhof
 */
class MontiThingsToolTest {
  protected static final String RELATIVE_MODEL_PATH = "src/test/resources";

  protected static final String PACKAGE = "montithings/tool";

  protected static final String TOOL_NAME = "MontiThingsTool";

  private static final Path TARGETPATH = Paths.get("target/generated-test-sources/");

  @ParameterizedTest
  @ValueSource(strings = { "validExample" })
  public void shouldAcceptValidModels(String modelPathDirectory) {

    // given
    MontiThingsTool tool = new MontiThingsTool();
    File modelPath = Paths.get(RELATIVE_MODEL_PATH, PACKAGE, modelPathDirectory).toFile();
    File target = TARGETPATH.toFile();

    // when

    // 1. Find all .arc files
    List<String> foundModels = Modelfinder
      .getModelsInModelPath(modelPath, MontiThingsLanguage.FILE_ENDING);
    // 2. Initialize SymbolTable
    Log.info("Initializing symboltable", TOOL_NAME);
    String basedir = DirectoryUtil.getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(),
      target.getAbsolutePath());
    IMontiThingsScope symTab = tool.initSymbolTable(modelPath);

    List<ComponentTypeSymbol> foundComponents = new ArrayList<>();

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 3. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, TOOL_NAME);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      // 4. check cocos
      Log.info("Check model: " + qualifiedModelName, TOOL_NAME);
      tool.checkCoCos(comp.getAstNode());

      foundComponents.add(comp);
    }

    // then
    assertThat(foundComponents).hasSize(3);
    assertThat(foundComponents).doesNotContainNull();
    assertThat(foundComponents).anySatisfy(e -> assertThat(e.getName()).isEqualTo("Composed"));
    assertThat(foundComponents).anySatisfy(e -> assertThat(e.getName()).isEqualTo("InComp"));
    assertThat(foundComponents).anySatisfy(e -> assertThat(e.getName()).isEqualTo("OutComp"));
    ComponentTypeSymbol composed = foundComponents.stream()
      .filter(c -> c.getName().equals("Composed")).findAny().get();
    assertThat(composed.getSubComponents()).hasSize(2);
    assertThat(composed.getSubComponents()).anySatisfy(e -> assertThat(e.getName()).isEqualTo("ic"));
    assertThat(composed.getSubComponents()).anySatisfy(e -> assertThat(e.getName()).isEqualTo("oc"));
  }
}
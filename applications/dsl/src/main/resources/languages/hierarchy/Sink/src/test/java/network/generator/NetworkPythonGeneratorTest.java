// (c) https://github.com/MontiCore/monticore
package network.generator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import network.AbstractTest;
import network._ast.ASTNet;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author kirchhof
 */
class NetworkPythonGeneratorTest extends AbstractTest {
  @CsvSource({
      "src/test/resources/ValidInput2.net, src/test/resources/ExpectedNet.py" })
  @ParameterizedTest void shouldAcceptValidModel(String inputModel, String expectedPath) {
    // given
    ASTNet ast = parseModel(inputModel);
    Path expectedPythonFile = Paths.get(expectedPath);

    //when
    Path pythonFile = NetworkPythonGenerator.generate(ast);

    //then
    assertThat(pythonFile).exists();
    assertThat(pythonFile).hasSameContentAs(expectedPythonFile);
  }
}
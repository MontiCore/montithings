// (c) https://github.com/MontiCore/monticore
package network.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static java.util.Collections.*;

/**
 * TODO
 *
 * @author (last commit) kirchhof
 * @version 1.0, 20.03.19
 * @since 1.0
 */
class MinOneNodeTest extends AbstractCocoTest {

  private static final String MODEL_ROOT = "src/test/resources/";

  @Override public CocoTestInput prepareTest(String pathToModelFile) {
    CocoTestInput result = super.prepareTest(pathToModelFile);
    MinOneNode systemUnderTest = new MinOneNode();
    result.getChecker().addCoCo(systemUnderTest);
    return result;
  }

  @CsvSource({ "ValidInput.net",
      "ValidInput2.net"})
  @ParameterizedTest void shouldAcceptValidModel(String inputModel) {
    //given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputModel);

    //when
    executeCoCo(input);

    //then
    checkResults(EMPTY_LIST);
  }

  @CsvSource({ "InvalidModels/MinOneNodeTest.net"})
  @ParameterizedTest void shouldFailForInvalidModel(String inputModel) {
    //given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputModel);

    //when
    executeCoCo(input);

    //then
    checkResults(MinOneNode.THERE_ARE_TOO_FEW_NODES);
  }
}

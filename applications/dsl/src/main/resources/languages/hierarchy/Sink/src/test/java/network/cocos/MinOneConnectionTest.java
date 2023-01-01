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
class MinOneConnectionTest extends AbstractCocoTest {

  private static final String MODEL_ROOT = "src/test/resources/";

  @Override public CocoTestInput prepareTest(String pathToModelFile) {
    CocoTestInput result = super.prepareTest(pathToModelFile);
    MinOneConnection systemUnderTest = new MinOneConnection();
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

  @CsvSource({ "InvalidModels/MinOneConnectionTest.net"})
  @ParameterizedTest void shouldFailForInvalidModel(String inputModel) {
    //given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputModel);

    //when
    executeCoCo(input);

    //then
    checkResults(MinOneConnection.THERE_ARE_TOO_FEW_CONNECTIONS);
  }
}
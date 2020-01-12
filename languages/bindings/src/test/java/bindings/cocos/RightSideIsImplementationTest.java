// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static java.util.Collections.EMPTY_LIST;

public class RightSideIsImplementationTest extends AbstractCocoTest {

  private static final String MODEL_ROOT = "src/test/resources/";

  @Override public CocoTestInput prepareTest(String pathToModelFile) {
    CocoTestInput result = super.prepareTest(pathToModelFile);
    RightSideIsImplementation systemUnderTest = new RightSideIsImplementation();
    result.getChecker().addCoCo(systemUnderTest);
    return result;
  }

  @CsvSource({
      "bindings/ValidBinding.mtb"
  })
  @ParameterizedTest void shouldAcceptValidBinding(String inputBinding) {
    // given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

    // when
    executeCoCo(input);

    // then
    checkResults(EMPTY_LIST);

  }

  @CsvSource({
      "bindings/InvalidBinding.mtb"
  })
  @ParameterizedTest void shouldFailWithNoModelImplementation(String inputBinding) {
    // given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

    // when
    executeCoCo(input);

    // then
    checkResults(RightSideIsImplementation.NO_MODEL_IMPLEMENTATION);

  }

  @CsvSource({
      "bindings/WrongModel.mtb"
  })
  @ParameterizedTest void shouldFailWithInvalidBinding(String inputBinding) {
    // given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

    // when
    executeCoCo(input);

    // then
    checkResults(RightSideIsImplementation.RIGHT_SIDE_NO_IMPLEMENTATION);

  }

}

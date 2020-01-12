// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static java.util.Collections.EMPTY_LIST;

public class ImplementationHasSamePortsAsInterfaceTest extends AbstractCocoTest {
  private static final String MODEL_ROOT = "src/test/resources/";

  @Override public CocoTestInput prepareTest(String pathToModelFile) {
    CocoTestInput result = super.prepareTest(pathToModelFile);
    ImplementationHasSamePortsAsInterface systemUnderTest = new ImplementationHasSamePortsAsInterface();
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
      "bindings/WrongPortBinding.mtb"
  })
  @ParameterizedTest void shouldFailWithNotSamePortsImplemented(String inputBinding) {
    // given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

    // when
    executeCoCo(input);

    // then
    checkResults(ImplementationHasSamePortsAsInterface.NOT_SAME_PORTS_IMPLEMENTED);

  }

  @CsvSource({
      "bindings/DifferentPortDeclarationBinding.mtb"
  })
  @ParameterizedTest void shouldAcceptDifferentPortDeclaration(String inputBinding) {
    // given
    CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

    // when
    executeCoCo(input);

    // then
    checkResults(EMPTY_LIST);

  }
}

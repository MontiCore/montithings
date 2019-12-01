package bindings.cocos;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class ImplementationExistsTest extends AbstractCocoTest {

    private static final String MODEL_ROOT = "src/test/resources/";

    @Override public CocoTestInput prepareTest(String pathToModelFile) {
        CocoTestInput result = super.prepareTest(pathToModelFile);
        ImplementationExists systemUnderTest = new ImplementationExists();
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
    @ParameterizedTest void shouldFailWithInvalidBinding(String inputBinding) {
        // given
        CocoTestInput input = prepareTest(MODEL_ROOT + inputBinding);

        // when
        executeCoCo(input);

        // then
        checkResults(ImplementationExists.NO_MODEL_IMPLEMENTATION);

    }

}

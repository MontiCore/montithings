package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class PortsInBehaviorAreUsedCorrectlyTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
            .addCoCo(new PortsInBehaviorAreUsedCorrectly());
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
            Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.InvalidIncomingPortInBehavior",
                    2,
                    new MontiThingsError[] {  }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.OutgoingPortInBehaviorSpecification",
                    1,
                    new MontiThingsError[] {  }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.NotAllIncomingPortsInBehaviors",
                    1,
                    new MontiThingsError[] {  }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.RepeatedSetOfPorts",
                    1,
                    new MontiThingsError[] {  }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.PreviousBehaviorPortsAreSubset",
                    1,
                    new MontiThingsError[] {  }
            )
    );
  }
}
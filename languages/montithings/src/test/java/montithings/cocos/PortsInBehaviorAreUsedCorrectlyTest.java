package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class PortsInBehaviorAreUsedCorrectlyTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    MontiThingsCoCoChecker cocos = new MontiThingsCoCoChecker();
    cocos.addCoCo(new PortsInBehaviorAreUsedCorrectly());
    return cocos;
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
                    new MontiThingsError[] { MontiThingsError.BEHAVIOR_USES_UNDECLARED_PORT }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.OutgoingPortInBehaviorSpecification",
                    1,
                    new MontiThingsError[] { MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.NotAllIncomingPortsInBehaviors",
                    0,
                    new MontiThingsError[] { MontiThingsError.INCOMING_PORTS_NOT_USED }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.RepeatedSetOfPorts",
                    1,
                    new MontiThingsError[] { MontiThingsError.MULTIPLE_BEHAVIORS_SAME_PORTS }
            ),
            Arguments.of(getChecker(),
                    "cocoTest.portsInBehaviorAreUsedCorrectly.invalid.PreviousBehaviorPortsAreSubset",
                    0,
                    new MontiThingsError[] { MontiThingsError.BEHAVIOR_PORTS_USED_ALREADY }
            )
    );
  }
}
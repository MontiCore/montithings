package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class NoIncomingPortsInEveryBlocksTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new NoIncomingPortsInEveryBlocks());
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.noIncomingPortsInEveryBlocks.invalid.Sink",
        1,
        new MontiThingsError[] { MontiThingsError.NO_INCOMING_PORTS_IN_EVERY_BLOCK }
      ),
      Arguments.of(getChecker(),
        "cocoTest.noIncomingPortsInEveryBlocks.invalidLog.Sink",
        1,
        new MontiThingsError[] { MontiThingsError.NO_INCOMING_PORTS_IN_EVERY_BLOCK_LOG }
      )
    );
  }
}
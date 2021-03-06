// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import montiarc._ast.ASTMACompilationUnit;
import montiarc._parser.MontiArcParser;
import montithings.AbstractTest;
import montithings._parser.MontiThingsParser;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MontiThingsPrettyPrinterDelegatorTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("validInput")
  public void prettyPrintedAstShouldMatchOriginalAst(String filename) throws IOException {
    // given
    MontiThingsParser parser = new MontiThingsParser();
    final Optional<ASTMACompilationUnit> ast = parser.parse(filename);
    final MontiThingsFullPrettyPrinter printer = new MontiThingsFullPrettyPrinter();
    assertThat(ast).isPresent();

    // when
    String output = printer.prettyprint(ast.get());

    // then
    final Optional<ASTMACompilationUnit> astPrint = parser.parse_StringMACompilationUnit(output);
    assertTrue(astPrint.isPresent());
    assertTrue(ast.get().deepEquals(astPrint.get()));
  }

  @ParameterizedTest
  @MethodSource("validInput")
  public void shouldConvertMT2MA(String filename) throws IOException {
    // given
    MontiThingsParser mtParser = new MontiThingsParser();
    final Optional<ASTMACompilationUnit> ast = mtParser.parse(filename);
    final MontiThingsToMontiArcFullPrettyPrinter printer = new MontiThingsToMontiArcFullPrettyPrinter();
    assertThat(ast).isPresent();

    // when
    String output = printer.prettyprint(ast.get());

    // then
    MontiArcParser maParser = new MontiArcParser();
    maParser.parse_StringMACompilationUnit(output);
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("src/test/resources/models/cocoTest/valid/Converter.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/Example.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/LowPassFilter.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/Sink.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/Source.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/math/Doubler.mt"),
      Arguments.of("src/test/resources/models/cocoTest/valid/math/Sum.mt")
    );
  }

  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }
}
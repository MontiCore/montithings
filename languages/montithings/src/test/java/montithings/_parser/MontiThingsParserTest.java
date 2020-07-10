package montithings._parser;

import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.MontiArcError;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author kirchhof
 */
class MontiThingsParserTest extends AbstractTest {
  protected static final String PACKAGE = "montithings/_parser";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiArcError.ERROR_CODE_PATTERN;
  }

  static public Optional<ASTMACompilationUnit> parse(String relativeFilePath) {
    return parse(relativeFilePath, false);
  }

  static public Optional<ASTMACompilationUnit> parse(String relativeFilePath,
      boolean expParserErrors) {
    MontiThingsParser parser = new MontiThingsParser();
    Optional<ASTMACompilationUnit> optAst;
    try {
      optAst = parser.parse(relativeFilePath);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (expParserErrors) {
      assertThat(parser.hasErrors()).isTrue();
      assertThat(optAst).isNotPresent();
    }
    else {
      if (parser.hasErrors()) {
        System.err.println(Log.getFindings().toString());
      }
      assertThat(parser.hasErrors()).isFalse();
      assertThat(optAst).isPresent();
    }
    return optAst;
  }

  @ParameterizedTest
  @CsvSource({ "valid/Composed.mt", "valid/Sink.mt", "valid/PrePostcondition.mt",
      "valid/PortExtensions.mt", "valid/SetExpressions.mt", "valid/Timing.mt" })
  public void shouldParseWithoutError(String fileName) {
    parse(Paths.get(RELATIVE_MODEL_PATH, PACKAGE, fileName).toString(), false);
  }
}
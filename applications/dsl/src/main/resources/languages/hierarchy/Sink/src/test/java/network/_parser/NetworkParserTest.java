// (c) https://github.com/MontiCore/monticore
package network._parser;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import network._ast.ASTNet;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the parser reads in valid input files
 *
 * @author (last commit) kirchhof
 * @version 1.0, 19.03.2019
 * @since 1.0
 */
public class NetworkParserTest {

  @Nested class Parse {
    @CsvSource({"src/test/resources/ValidInput.net",
        "src/test/resources/ValidInput2.net"})
    @ParameterizedTest void shouldParseFile(String pathToInput) throws IOException {
      //given
      NetworkParser parser = new NetworkParser();

      //when
      Optional<ASTNet> result = parser.parse(pathToInput);

      //then
      assertThat(result).isPresent();
    }
  }
}

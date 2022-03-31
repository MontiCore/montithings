// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author kirchhof
 */
class ComponentHelperTest {

  @ParameterizedTest
  @CsvSource({
    "String, std::string",
    "Double, double",
    "Stringify, Stringify",
    "std::string, std::string",
    "std::string, std::string",
    "List<String>, collections::list<std::string>",
    "List<Stringer>, collections::list<Stringer>",
    "List<List<String>>, collections::list<collections::list<std::string>>"
  })
  void java2cppTypeString(String given, String expected) {
    // given
    // when
    String actualResult = TypesHelper.java2cppTypeString(given);

    // then
    assertThat(actualResult).isEqualTo(expected);
  }
}
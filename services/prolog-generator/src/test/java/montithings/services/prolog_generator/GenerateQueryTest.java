// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator;

import montithings.services.prolog_generator.config._ast.ASTConfig;
import montithings.services.prolog_generator.config._parser.ConfigParser;
import montithings.services.prolog_generator.config.generator.QueryGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenerateQueryTest {

    @Test
    public void testParseConfig() throws IOException {
        Path model = Paths.get("src/test/resources/iot-config/config_example.json");
        ConfigParser parser = new ConfigParser();


        Optional<ASTConfig> jsonDoc = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(jsonDoc.isPresent());

    }

    @Test
    public void testGenerateQuery() throws IOException {
        Path model = Paths.get("src/test/resources/iot-config/config_example.json");
        ConfigParser parser = new ConfigParser();


        Optional<ASTConfig> jsonDoc = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(jsonDoc.isPresent());


        QueryGenerator.generateQuery(jsonDoc.get());
    }
}
// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator;

import montithings.services.prolog_generator.facts._ast.ASTDevices;
import montithings.services.prolog_generator.facts._parser.FactsParser;
import montithings.services.prolog_generator.facts.generator.FactsGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenerateFactsTest {

    @Test
    public void testParseFacts() throws IOException {
        Path model = Paths.get("src/test/resources/iot-config/facts.json");
        FactsParser parser = new FactsParser();


        Optional<ASTDevices> jsonDoc = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(jsonDoc.isPresent());

    }

    @Test
    public void testGenerateFacts() throws IOException {
        Path model = Paths.get("src/test/resources/iot-config/facts.json");
        FactsParser parser = new FactsParser();


        Optional<ASTDevices> jsonDoc = parser.parse(model.toString());
        assertFalse(parser.hasErrors());
        assertTrue(jsonDoc.isPresent());


        FactsGenerator.generateFacts(jsonDoc.get());
    }
}
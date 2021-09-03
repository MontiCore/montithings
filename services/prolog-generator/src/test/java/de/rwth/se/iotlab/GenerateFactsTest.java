package de.rwth.se.iotlab;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.lang.json._parser.JSONParser;
import de.rwth.se.iotlab.facts._ast.ASTDevices;
import de.rwth.se.iotlab.facts._ast.ASTFactsNode;
import de.rwth.se.iotlab.facts._parser.FactsParser;
import de.rwth.se.iotlab.facts.generator.FactsGenerator;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;

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
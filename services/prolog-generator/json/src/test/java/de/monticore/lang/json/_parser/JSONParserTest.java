/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.json._ast.ASTJSONDocument;

public class JSONParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/bookstore.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testGeneratedArray() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/generatedArray.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testGeneratedObjects() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/generatedObjects.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
  @Test
  public void testNumbers() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/parser/Numbers.json");
    JSONParser parser = new JSONParser();
    
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.json._ast.ASTJSONDocument;
import de.monticore.lang.json._parser.JSONParser;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;

public class JSONSymboltableTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/symboltable/bookstore.json");
    JSONParser parser = new JSONParser();
    
    // parse model
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    final JSONLanguage lang = JSONSymTabMill.jSONLanguageBuilder().build();
    JSONArtifactScope scope = createSymbolTable(lang, jsonDoc.get());
    
    // test resolving
    assertTrue(scope.resolveJSONProperty("bookstore").isPresent());
    assertTrue(scope.resolveJSONProperty("bookstore.title").isPresent());
    assertTrue(scope.resolveJSONProperty("bookstore.order.more Books.SpecifiedBook.someNumber").isPresent());
    
    Optional<JSONPropertySymbol> bookOpt = scope.resolveJSONProperty("bookstore.order.more Books.SpecifiedBook");
    assertTrue(bookOpt.isPresent());
    assertTrue(bookOpt.get().getSpannedScope().resolveJSONProperty("Definition").isPresent());
    assertTrue(bookOpt.get().getEnclosingScope().resolveJSONPropertyDown("SpecifiedBook.Definition").isPresent());
  }

  @Test
  public void testDeepResolving() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/symboltable/PingPong.json");
    JSONParser parser = new JSONParser();
    
    // parse model
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    final JSONLanguage lang = JSONSymTabMill.jSONLanguageBuilder().build();
    JSONArtifactScope scope = createSymbolTable(lang, jsonDoc.get());
    
    // test resolving
    // we only test the resolveDown methods as the other methods are not
    // modified (i.e., resolve and resolve locally methods are not affected)

    // test if default qualified resolveDown behavior is preserved
    assertTrue(scope.resolveJSONPropertyDown("PingPong.InGame.Ping").isPresent());
    assertTrue(scope.resolveJSONPropertyDown("PingPong.very.deep.substate").isPresent());
    
    // test deep resolving with unqualified symbol name
    assertTrue(scope.resolveJSONPropertyDown("substate").isPresent());
    
    // test unqualified resolving with multiple occurrences: 2 Ping symbols
    assertEquals(scope.resolveJSONPropertyDownMany("Ping").size(), 2, 0);
    
    // test negative case, where we try to resolve one Ping property
    boolean success = true;
    try {
      scope.resolveJSONPropertyDown("Ping");
    }
    catch (ResolvedSeveralEntriesForSymbolException e) {
      success = false;
    }
    assertFalse(success);
    
    // test "half"-qualified down resolving. We pass an incomplete qualification
    // for symbol Ping. Expected behavior: we handle the name as fully qualified
    // until there is only one part left and continue with deep resolving in all
    // sub-property. In this test case, we navigate to the scope spanning symbol
    // "very". From here, the symbol Ping lies several scopes beneath. However,
    // since Ping is uniquely accessible from this point, no error occurs and we
    // find exactly one symbol.
    assertTrue(scope.resolveJSONPropertyDown("PingPong.very.Ping").isPresent());
    
    // test down resolving with in-between steps
    Optional<JSONPropertySymbol> deep_sym = scope.resolveJSONProperty("PingPong.deep");
    IJSONScope deep_scope = deep_sym.get().getSpannedScope();
    assertTrue(deep_scope.resolveJSONPropertyDown("substate").isPresent());
  }
  
  @Test
  public void testAddresses() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/json/symboltable/addresses.json");
    JSONParser parser = new JSONParser();
    
    // parse model
    Optional<ASTJSONDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
    
    // build symbol table
    final JSONLanguage lang = JSONSymTabMill.jSONLanguageBuilder().build();
    JSONArtifactScope scope = createSymbolTable(lang, jsonDoc.get());
    
    // test resolving
    assertTrue(scope.resolveJSONProperty("Alice.address.postal_code").isPresent());
    assertEquals(scope.resolveJSONPropertyDownMany("postal_code").size(), 2);
  }
  
  /**
   * Creates the symbol table from the parsed AST.
   *
   * @param lang The JSON language.
   * @param ast The top JSON model element.
   * @return The artifact scope derived from the parsed AST
   */
  private static JSONArtifactScope createSymbolTable(JSONLanguage lang, ASTJSONDocument ast) {
    JSONGlobalScope globalScope = JSONSymTabMill.jSONGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setJSONLanguage(lang)
        .build();
    JSONSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }
  
}

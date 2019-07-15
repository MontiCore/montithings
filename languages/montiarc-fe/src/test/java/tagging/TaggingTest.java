/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package tagging;

import de.monticore.lang.tagging._symboltable.TagSymbol;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;
import infrastructure.AbstractCoCoTest;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc.tagging.distribution.ConnectionSymbol;
import montiarc.tagging.distribution.DistributionSchema;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Testing for tagged MA components
 *
 * @author (last commit) JFuerste
 */
public class TaggingTest extends AbstractCoCoTest {

  protected static TaggingResolver createSymTabAndTaggingResolver(File... modelPaths) {
    List<String> paths = Arrays.asList(modelPaths).stream().map(x -> x.getPath()).collect(Collectors.toList());
    Scope scope = MONTIARCTOOL.initSymbolTable(modelPaths);
    TaggingResolver tagging = new TaggingResolver(scope, Arrays.asList(modelPaths));
    DistributionSchema.registerTagTypes(tagging);
    return tagging;
  }


  @Test
  public void testDistributionParsing(){
    TaggingResolver taggingResolver = createSymTabAndTaggingResolver(Paths.get(MODEL_PATH).toFile());
    ComponentSymbol comp = taggingResolver.<ComponentSymbol>resolve("tagging.DistributionPortTag", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);
    PortSymbol in1 = comp.getIncomingPort("in1").orElse(null);
    assertNotNull(in1);

    Collection<TagSymbol> tags = taggingResolver.getTags(in1, ConnectionSymbol.KIND);
    assertTrue(tags.size() == 1);
    assertTrue(in1.getConnectionSymbolOpt().isPresent());
    assertTrue(in1.getConnectionValueOpt().isPresent());
    System.out.println(in1.getConnectionValueOpt().get());
  }
}

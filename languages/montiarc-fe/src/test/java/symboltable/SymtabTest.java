/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.ValueSymbol;
import montiarc.helper.SymbolPrinter;

/**
 * Tests for symbol table of MontiArc.
 *
 * @author Robert Heim
 */
public class SymtabTest {

  private static MontiArcTool tool;

  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    tool = new MontiArcTool();
  }

  @Test
  public void testResolveJavaDefaultTypes() {
    Scope symTab = tool.initSymbolTable(Paths.get("src/test/resources/").toFile(), Paths.get("src/main/resources/defaultTypes").toFile());

    Optional<JTypeSymbol> javaType = symTab.resolve("String", JTypeSymbol.KIND);
    assertFalse(
        "java.lang types may not be resolvable without qualification in general (e.g., global scope).",
        javaType.isPresent());

    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "components.body.subcomponents.ComponentWithNamedInnerComponent", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    // java.lang.*
    javaType = comp.getSpannedScope().resolve("String", JTypeSymbol.KIND);
    assertTrue("java.lang types must be resolvable without qualification within components.",
        javaType.isPresent());

    // java.util.*
    javaType = comp.getSpannedScope().resolve("Set", JTypeSymbol.KIND);
    assertTrue("java.util types must be resolvable without qualification within components.",
        javaType.isPresent());

  }
  
  @Ignore("ValueSymbol?!")
  @Test
  public void testParametersSymtab() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingSCWithParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    //assertEquals(0, Log.getErrorCount());
    // TODO portusage coco
    // assertEquals(1, Log.getFindings().stream().filter(f -> f.isWarning()).count());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("deleteTempFile", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("deleteTempFile", delay.getName());

    assertEquals(1, delay.getConfigArguments().size());
    assertEquals("1", delay.getConfigArguments().get(0).getValue());

    //Is an expression since there is no value symbol.
    assertEquals(ValueSymbol.Kind.Value,
        delay.getConfigArguments().get(0).getKind());
  }

  /**
   * TODO: ValueSymbol?!
   */
  @Test
  public void testComplexParametersSymtab() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingComplexParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    assertEquals(0, Log.getErrorCount());
    Assert.assertEqualErrorCounts(new ArrayList<Finding>(),
        Log.getFindings().stream().filter(f -> f.isWarning()).collect(Collectors.toList()));

    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());

    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}",
        SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    // TODO value symbol
    // assertEquals(ValueSymbol.Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());
    // assertEquals("new HashMap<List<String>, List<Integer>>()",
    // delay.getConfigArguments().get(1).getValue());
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // JTypeReference<? extends JTypeSymbol> typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("java.lang.String",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("java.lang.Integer",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());
  }

  /**
   * TODO: ValueSymbol!?
   */
  @Test
  public void testGenericParametersSymtab3() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol comp = symTab.<ComponentSymbol> resolve(
        "params.UsingComplexGenericParams", ComponentSymbol.KIND).orElse(null);
    assertNotNull(comp);

    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    ComponentInstanceSymbol delay = (ComponentInstanceSymbol) comp.getSpannedScope()
        .resolve("cp", ComponentInstanceSymbol.KIND).orElse(null);
    assertNotNull(delay);
    assertEquals("cp", delay.getName());

    assertEquals(2, delay.getConfigArguments().size());
    assertEquals("new int[] {1, 2, 3}", SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(0)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(0).getKind());
    // assertEquals("1",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(0).getValue());
    // assertEquals("2",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(1).getValue());
    // assertEquals("3",
    // delay.getConfigArguments().get(0).getConstructorArguments().get(2).getValue());

    assertEquals("new HashMap<List<K>, List<V>>()", SymbolPrinter.printConfigArgument(delay.getConfigArguments().get(1)));
    // TODO value symbol
    // assertEquals(Kind.ConstructorCall, delay.getConfigArguments().get(1).getKind());
    // ArcdTypeReferenceEntry typeRef = delay.getConfigArguments().get(1).getType();
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(0).getType().getName());
    // assertEquals("java.util.List", typeRef.getTypeParameters().get(1).getType().getName());
    // assertEquals("K",
    // typeRef.getTypeParameters().get(0).getTypeParameters().get(0).getType().getName());
    // assertEquals("V",
    // typeRef.getTypeParameters().get(1).getTypeParameters().get(0).getType().getName());

  }

  @Test
  public void testPortStereoType() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    PortSymbol port = symTab.<PortSymbol> resolve("a.Sub1.integerIn", PortSymbol.KIND).orElse(null);
    assertNotNull(port);

    assertEquals(3, port.getStereotype().size());
    assertEquals("held", port.getStereotype().get("disabled").get());
    assertEquals("1", port.getStereotype().get("initialOutput").get());
    assertFalse(port.getStereotype().get("ignoreWarning").isPresent());
  }

  @Test
  public void testConnectorStereoType() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ConnectorSymbol connector = symTab
        .<ConnectorSymbol> resolve("a.Sub1.stringOut", ConnectorSymbol.KIND).orElse(null);
    assertNotNull(connector);

    assertEquals(1, connector.getStereotype().size());
    assertFalse(connector.getStereotype().get("conStereo").isPresent());
  }

  @Test
  public void testComponentEntryIsDelayed() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "timing.Timing", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);

    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

    ComponentSymbol child = parent.getInnerComponent("TimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimedDelayingInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("TimeSyncInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());

    child = parent.getInnerComponent("TimeCausalSyncInner").orElse(null);
    assertNotNull(child);
    assertTrue(child.hasDelay());

    child = parent.getInnerComponent("UntimedInner").orElse(null);
    assertNotNull(child);
    assertFalse(child.hasDelay());
  }

  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToPort() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "ocl.OCLFieldToPort", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);

    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());
  }

  @Ignore("TODO ocl invariants?")
  @Test
  public void testAdaptOCLFieldToArcdField() {
    Scope symTab = tool.initSymbolTable("src/test/resources/arc/symtab");
    ComponentSymbol parent = symTab.<ComponentSymbol> resolve(
        "ocl.OCLFieldToArcField", ComponentSymbol.KIND).orElse(null);
    assertNotNull(parent);

    assertEquals(0, Log.getErrorCount());
    assertEquals(0, Log.getFindings().stream().filter(f -> f.isWarning()).count());

  }
}

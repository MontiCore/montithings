// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.*;
import behavior._ast.ASTLogStatement;
import behavior._ast.ASTLogStatementBuilder;
import de.monticore.ast.Comment;
import de.monticore.ast.CommentBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTArgumentsBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.*;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMACompilationUnitBuilder;
import montithings.MontiThingsMill;
import montithings._ast.*;
import montithings._auxiliary.ComfortableArcMillForMontiThings;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.util.GenericBindingUtil.printSimpleType;

public abstract class BasicTransformations {
  /**
   * @param comp   AST of component which is modified
   * @param source Qualified port name on the left side, e.g. source.value
   * @param target Qualified port name on the right side, e.g. sink.value
   */
  protected void addConnection(ASTMACompilationUnit comp, String source, String target) {
    addConnection(comp.getComponentType(), source, target);
  }

  /**
   * @param comp   AST of component type which is modified
   * @param source Qualified port name on the left side, e.g. source.value
   * @param target Qualified port name on the right side, e.g. sink.value
   */
  protected void addConnection(ASTComponentType comp, String source, String target) {
    ASTConnectorBuilder connectorBuilder = ComfortableArcMillForMontiThings.connectorBuilder();
    connectorBuilder.setSource(source);
    connectorBuilder.addTarget(target);
    comp.getBody().addArcElement(connectorBuilder.build());
  }

  /**
   * Searches and removes connection defined in the given component with the source port on the left hand side and
   * the target port at the right hand side. If the connection is not defined, nothing will be removed.
   *
   * @param comp   AST of component which is modified
   * @param source AST of source port
   * @param target AST of target port
   */
  protected void removeConnection(ASTMACompilationUnit comp, ASTPortAccess source,
    ASTPortAccess target) {
    Optional<ASTConnector> match =
      comp.getComponentType().getConnectors().stream()
        .filter(conn -> conn.getTargetList().contains(target))
        .filter(conn -> conn.getSource().equals(source))
        .findFirst();
    match
      .ifPresent(astConnector -> comp.getComponentType().getBody().removeArcElement(astConnector));
  }

  /**
   * Add a port to the given component.
   *
   * @param comp       AST of component which is modified
   * @param name       Name of the port
   * @param isOutgoing Defines the direction of the port
   * @param type       Defines the type of the port
   */
  protected void addPort(ASTMACompilationUnit comp, String name, Boolean isOutgoing,
    ASTMCType type) {
    ASTPortDeclarationBuilder portDeclarationBuilder = ComfortableArcMillForMontiThings
      .portDeclarationBuilder();
    portDeclarationBuilder
      .setIncoming(!isOutgoing)
      .addPort(name)
      .setMCType(type);

    ASTComponentInterfaceBuilder astComponentInstanceBuilder = ComfortableArcMillForMontiThings
      .componentInterfaceBuilder();

    astComponentInstanceBuilder.addPortDeclaration(portDeclarationBuilder.build());
    ASTComponentInterface build = astComponentInstanceBuilder.build();
    comp.getComponentType().getBody().addArcElement(build);
  }

  /**
   * Add a port to the given component.
   *
   * @param comp       AST of component which is modified
   * @param name       Name of the port
   * @param isOutgoing Defines the direction of the port
   * @param type       Defines the type of the port, given as SymTypeExpression
   */
  protected void addPort(ASTMTComponentType comp, String name, Boolean isOutgoing,
      SymTypeExpression type) {
    ASTMCQualifiedName qualifiedName =
        MontiThingsMill.mCQualifiedNameBuilder().addParts(type.print()).build();
    ASTMCType mcType =
        MontiThingsMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
    ASTPortDeclarationBuilder portDeclarationBuilder = ComfortableArcMillForMontiThings
        .portDeclarationBuilder();
    portDeclarationBuilder
        .setIncoming(!isOutgoing)
        .addPort(name)
        .setMCType(mcType);

    ASTComponentInterfaceBuilder astComponentInstanceBuilder = ComfortableArcMillForMontiThings
        .componentInterfaceBuilder();

    astComponentInstanceBuilder.addPortDeclaration(portDeclarationBuilder.build());
    ASTComponentInterface build = astComponentInstanceBuilder.build();
    comp.getBody().addArcElement(build);
  }

  /**
   * Creates and returns a new empty compilation unit.
   *
   * @param packageId Package which the new component should belong to
   * @param typeName  Name of the component type, e.g. Source
   * @return ASTMACompilationUnit of the newly created component
   */
  protected ASTMACompilationUnit createCompilationUnit(ASTMCQualifiedName packageId,
    String typeName) {
    ASTMACompilationUnitBuilder compBuilder = MontiThingsMill.mACompilationUnitBuilder();
    compBuilder.setPackage(packageId);

    ASTComponentHeadBuilder headBuilder = ComfortableArcMillForMontiThings.componentHeadBuilder();
    ASTComponentBodyBuilder bodyBuilder = ComfortableArcMillForMontiThings.componentBodyBuilder();
    ASTMTComponentModifierBuilder componentModifier = MontiThingsMill.mTComponentModifierBuilder();

    ASTMTComponentTypeBuilder typeBuilder = MontiThingsMill.mTComponentTypeBuilder();
    typeBuilder.setHead(headBuilder.build());
    typeBuilder.setBody(bodyBuilder.build());
    typeBuilder.setName(typeName);
    typeBuilder.setMTComponentModifier(componentModifier.build());
    compBuilder.setComponentType(typeBuilder.build());

    return compBuilder.build();
  }

  /**
   * Adds a given qualified name to the imports of the given component
   *
   * @param packageId ASTMCQualifiedName of the imported component
   * @param comp      Component which should import the packageId
   */
  protected void addImportStatement(ASTMCQualifiedName packageId, ASTMACompilationUnit comp) {
    ASTMCImportStatement importStatement = MontiThingsMill.mCImportStatementBuilder()
      .setMCQualifiedName(packageId).build();

    comp.addImportStatement(importStatement);
  }

  /**
   * Adds an empty behavior java block to the given component.
   *
   * @param comp AST of component which is modified
   * @return empty behavior block
   */
  protected ASTBehavior addEmptyBehavior(ASTMACompilationUnit comp) {
    ASTMCJavaBlock javaBlock = MontiThingsMill.mCJavaBlockBuilder().build();

    ASTBehaviorBuilder behavior = MontiThingsMill.behaviorBuilder();
    behavior.setMCJavaBlock(javaBlock);
    ASTBehavior build = behavior.build();
    comp.getComponentType().getBody().addArcElement(build);

    return build;
  }

  /**
   * @param comp         AST of component which is modified
   * @param qName        Type name
   * @param instanceName Instance name
   * @param args         Instance arguments
   * @return component instantiation
   */
  protected ASTComponentInstantiation addSubComponentInstantiation(ASTMACompilationUnit comp,
    ASTMCQualifiedName qName, String instanceName, ASTArguments args) {
    ASTComponentInstantiationBuilder instantiationBuilder =
      ComfortableArcMillForMontiThings.componentInstantiationBuilder();

    instantiationBuilder.addInstance(instanceName);
    instantiationBuilder.getComponentInstance(0).setArguments(args);
    instantiationBuilder.setMCType(createCompilationUnitType(qName.getBaseName()));

    ASTComponentInstantiation instantiation = instantiationBuilder.build();
    comp.getComponentType().getBody().addArcElement(instantiation);

    addImportStatement(qName, comp);

    return instantiation;
  }

  protected void addLongFieldDeclaration(ASTMACompilationUnit comp, String name, long value) {
    ASTNatLiteralBuilder natLiteralBuilder = MontiThingsMill.natLiteralBuilder();
    natLiteralBuilder.setDigits(String.valueOf(value));

    ASTLiteralExpressionBuilder literalExpressionBuilder =
      MontiThingsMill.literalExpressionBuilder();
    literalExpressionBuilder.setLiteral(natLiteralBuilder.build());

    ASTMCPrimitiveTypeBuilder mcPrimitiveTypeBuilder = MontiThingsMill.mCPrimitiveTypeBuilder();
    mcPrimitiveTypeBuilder.setPrimitive(ASTConstantsMCBasicTypes.LONG);

    ASTArcFieldDeclarationBuilder fieldDeclarationBuilder =
      MontiThingsMill.arcFieldDeclarationBuilder();
    fieldDeclarationBuilder.addArcField(name, literalExpressionBuilder.build());
    fieldDeclarationBuilder.setMCType(mcPrimitiveTypeBuilder.build());

    comp.getComponentType().getBody().addArcElement(fieldDeclarationBuilder.build());
  }

  protected ASTLogStatement createLogStatement(String content) {
    ASTStringLiteral stringLiteral = MontiThingsMill.stringLiteralBuilder().setSource(content)
      .build();

    ASTLogStatementBuilder logStatementBuilder = MontiThingsMill.logStatementBuilder();
    logStatementBuilder.setStringLiteral(stringLiteral);
    return logStatementBuilder.build();
  }

  /**
   * Searches and replaces the types of matching instantiations
   *
   * @param comp      AST of component which is modified
   * @param modelPath Path of the models which is used to gather the fully qualified name of components
   * @param type      The type which should get replaced
   * @param newType   The new type
   */
  protected void replaceComponentInstantiationType(ASTMACompilationUnit comp, File modelPath,
    String type, String newType) throws Exception {
    for (ASTComponentInstantiation subComponentInstantiation : comp.getComponentType()
      .getSubComponentInstantiations()) {
      String typeSubComp = printSimpleType(subComponentInstantiation.getMCType());
      if (type.equals(typeSubComp)) {
        subComponentInstantiation.setMCType(createCompilationUnitType(newType));
      }
    }

    ASTMCQualifiedName qNameOldType = TrafoUtil.getFullyQNameFromImports(modelPath, comp, type);
    ASTMCQualifiedName qNameNewType = TrafoUtil.copyASTMCQualifiedName(qNameOldType);
    qNameNewType.removeParts(qNameNewType.sizeParts() - 1);
    qNameNewType.addParts(newType);
    addImportStatement(qNameNewType, comp);
  }

  /**
   * Returns all instantiations of a given type within the given component
   *
   * @param comp AST of component which is searched in
   * @param type The type as a string which is searched for
   * @return List<ASTComponentInstantiation> with the given type
   */
  protected List<ASTComponentInstantiation> getInstantiationsByType(ASTMACompilationUnit comp,
    String type) {
    return comp.getComponentType().getSubComponentInstantiations().stream()
      .filter(i -> printSimpleType(i.getMCType()).equals(type))
      .collect(Collectors.toList());
  }

  protected ASTArguments createEmptyArguments() {
    ASTArgumentsBuilder builder = new ASTArgumentsBuilder();
    return builder.build();
  }

  protected ASTArguments createArguments(List<String> args) {
    ASTStringLiteralBuilder stringLiteralBuilder = new ASTStringLiteralBuilder();
    ASTLiteralExpressionBuilder literalExpressionBuilder = new ASTLiteralExpressionBuilder();
    ASTArgumentsBuilder argumentsBuilder = new ASTArgumentsBuilder();

    for (String arg : args) {
      stringLiteralBuilder.setSource(arg);
      literalExpressionBuilder.setLiteral(stringLiteralBuilder.build());
      argumentsBuilder.addExpression(literalExpressionBuilder.build());
    }

    return argumentsBuilder.build();
  }

  /**
   * Implements "varName += 1;"
   */
  protected ASTMCBlockStatement createIncrementVariableStatement(String varName) {
    ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName(varName)
      .build();

    ASTNatLiteral oneNatLiteral = MontiThingsMill.natLiteralBuilder().setDigits("1").build();
    ASTLiteralExpressionBuilder incrementIndexExpressionBuilder =
      MontiThingsMill.literalExpressionBuilder();
    incrementIndexExpressionBuilder.setLiteral(oneNatLiteral);

    ASTAssignmentExpressionBuilder incrementAssignmentExpressionBuilder =
      MontiThingsMill.assignmentExpressionBuilder();
    incrementAssignmentExpressionBuilder.setLeft(indexNameExpression);
    incrementAssignmentExpressionBuilder.setOperator(ASTConstantsAssignmentExpressions.PLUSEQUALS);
    incrementAssignmentExpressionBuilder.setRight(incrementIndexExpressionBuilder.build());

    ASTExpressionStatementBuilder incrementExpressionStatementBuilder =
      MontiThingsMill.expressionStatementBuilder();
    incrementExpressionStatementBuilder.setExpression(incrementAssignmentExpressionBuilder.build());

    return incrementExpressionStatementBuilder.build();
  }

  /**
   * Implements "varName1 = varName2?;"
   */
  protected ASTMCBlockStatement createAssignmentStatement(String varName1, String varName2) {
    ASTNameExpression varName1NameExpression = MontiThingsMill.nameExpressionBuilder()
      .setName(varName1).build();
    ASTNameExpression varName2NameExpression = MontiThingsMill.nameExpressionBuilder()
      .setName(varName2).build();

    ASTIsPresentExpression isPresentExpression = MontiThingsMill.isPresentExpressionBuilder()
      .setNameExpression(varName2NameExpression)
      .build();

    ASTAssignmentExpressionBuilder assignmentExpressionBuilder = MontiThingsMill
      .assignmentExpressionBuilder();
    assignmentExpressionBuilder.setLeft(varName1NameExpression);
    assignmentExpressionBuilder.setOperator(ASTConstantsAssignmentExpressions.EQUALS);
    assignmentExpressionBuilder.setRight(isPresentExpression);

    ASTExpressionStatementBuilder expressionStatementBuilder = MontiThingsMill
      .expressionStatementBuilder();
    expressionStatementBuilder.setExpression(assignmentExpressionBuilder.build());

    return expressionStatementBuilder.build();
  }

  /**
   * Implements "methodName(arg1, arg2,...);"
   */
  protected ASTCallExpression createCallExpression(String methodName, ASTArguments args) {
    ASTNameExpression methodNameExpression = MontiThingsMill.nameExpressionBuilder()
      .setName(methodName).build();

    ASTCallExpressionBuilder storeMsgTsCallExpressionBuilder = MontiThingsMill
      .callExpressionBuilder();
    storeMsgTsCallExpressionBuilder.setExpression(methodNameExpression);
    storeMsgTsCallExpressionBuilder.setArguments(args);
    storeMsgTsCallExpressionBuilder.setName(methodName);

    return storeMsgTsCallExpressionBuilder.build();
  }

  /**
   * Creates a ASTMCQualifiedType with the given name.
   *
   * @param name Name of the type
   * @return Corresponding ASTMCQualifiedType
   */
  private ASTMCQualifiedType createCompilationUnitType(String name) {
    ASTMCQualifiedName astmcQualifiedName = MontiThingsMill.mCQualifiedNameBuilder()
      .setPartsList(Collections.singletonList(name))
      .build();
    return MontiThingsMill.mCQualifiedTypeBuilder().
      setMCQualifiedName(astmcQualifiedName).build();
  }

  /**
   * Flag component as generated by adding a comment
   */
  protected void flagAsGenerated(ASTMACompilationUnit comp) {
    Comment comment = new CommentBuilder().setText("RECORD_AND_REPLAY_GENERATED").build();
    comp.getComponentType().getHead().add_PreComment(0, comment);
  }

  /**
   * Flag component as wrapped
   */
  protected void flagAsWrapped(ASTMACompilationUnit comp) {
    Comment comment = new CommentBuilder().setText("RECORD_AND_REPLAY_WRAPPED").build();
    comp.getComponentType().getHead().add_PreComment(0, comment);
  }

  protected boolean wasWrapped(ASTMACompilationUnit comp) {
    return comp.getComponentType().getHead().get_PreCommentList()
      .stream().anyMatch(c -> c.getText().equals("RECORD_AND_REPLAY_WRAPPED"));
  }
}

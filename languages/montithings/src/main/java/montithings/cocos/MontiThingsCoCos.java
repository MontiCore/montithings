/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;
import montiarc._cocos.*;
import montiarc.cocos.*;
import montithings._cocos.MontiThingsCoCoChecker;


/**
 * Context Conditions for MontiThings language
 *
 * @author (last commit) Joshua Fürste
 */
public class MontiThingsCoCos {
  public static MontiThingsCoCoChecker createChecker(){
    final MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker();
    return checker
            .addCoCo(new PortUsage())
            //TODO: Write proper type checking system
            //.addCoCo(new UsedTypesExist())
            .addCoCo(new SubComponentsConnected())
            .addCoCo(new SubcomponentParametersCorrectlyAssigned())
            .addCoCo(new PackageLowerCase())
            .addCoCo((MontiArcASTComponentCoCo) new NamesCorrectlyCapitalized())
            .addCoCo(new DefaultParametersHaveCorrectOrder())
            .addCoCo(new DefaultParametersCorrectlyAssigned())
            .addCoCo(new ComponentWithTypeParametersHasInstance())
            .addCoCo(new CircularInheritance())
            .addCoCo(new IOAssignmentCallFollowsMethodCall())
            .addCoCo(new AllGenericParametersOfSuperClassSet())
            .addCoCo(new TypeParameterNamesUnique())
            .addCoCo(new AmbiguousTypes())
            .addCoCo(new TopLevelComponentHasNoInstanceName())
            .addCoCo(new ConnectorEndPointIsCorrectlyQualified())
            .addCoCo(new InPortUniqueSender())
            .addCoCo(new ImportsValid())
            .addCoCo(new SubcomponentReferenceCycle())
            .addCoCo(new PortNamesAreNotJavaKeywords())
            .addCoCo(new montithings.cocos.UnusedImports())

            /// Automaton Cocos
            /// /////////////////////////////////////////////////////////////
            .addCoCo(new ImplementationInNonAtomicComponent())

            // CONVENTIONS
            .addCoCo((MontiArcASTBehaviorElementCoCo) new NamesCorrectlyCapitalized())
            .addCoCo(new AutomatonHasNoState())
            .addCoCo(new ArraysOfGenericTypes())
            .addCoCo(new AutomatonHasNoInitialState())
            .addCoCo(new MultipleAssignmentsSameIdentifier())
            .addCoCo(new AutomatonUsesCorrectPortDirection())
            .addCoCo((MontiArcASTInitialStateDeclarationCoCo) new AutomatonReactionWithAlternatives())
            .addCoCo((MontiArcASTTransitionCoCo) new AutomatonReactionWithAlternatives())
            .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfForbiddenExpression())
            .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfForbiddenExpression())
            .addCoCo((MontiArcASTPortCoCo) new UseOfProhibitedIdentifiers())
            .addCoCo((MontiArcASTVariableDeclarationCoCo) new UseOfProhibitedIdentifiers())
            .addCoCo((MontiArcASTParameterCoCo) new UseOfProhibitedIdentifiers())
            .addCoCo((MontiArcASTStateCoCo) new NamesCorrectlyCapitalized())
            .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
            //TODO: Reimplement this coco
            //.addCoCo(new ConnectorSourceAndTargetExistAndFit())
            .addCoCo(new ImportsAreUnique())

            // REFERENTIAL INTEGRITY
            .addCoCo(new UseOfUndeclaredState())
            .addCoCo((MontiArcASTIOAssignmentCoCo) new UseOfUndeclaredField())
            .addCoCo((MontiArcASTGuardExpressionCoCo) new UseOfUndeclaredField())
            .addCoCo(new SubcomponentGenericTypesCorrectlyAssigned())
            .addCoCo(new AssignmentHasNoName())
            .addCoCo(new ConfigurationParametersCorrectlyInherited())
            .addCoCo(new InnerComponentNotExtendsDefiningComponent())
            //.addCoCo(new UniqueTypeParamsInInnerCompHierarchy())

            // TYPE CORRECTNESS
            .addCoCo(new AutomatonGuardIsNotBoolean())
            .addCoCo(new GenericInitValues())
            .addCoCo(new ProhibitGenericsWithBounds())

            // .addCoCo(new AutomatonStimulusTypeDoesNotFitInputType())
            .addCoCo((MontiArcASTTransitionCoCo)new
                    AutomatonReactionTypeDoesNotFitOutputType())
            .addCoCo((MontiArcASTInitialStateDeclarationCoCo)new
                    AutomatonReactionTypeDoesNotFitOutputType())

            .addCoCo(new AutomatonNoDataAssignedToVariable())

            // UNIQUENESS OF NAMES
            .addCoCo(new AutomatonInitialDeclaredMultipleTimes())
            .addCoCo(new AutomatonStateDefinedMultipleTimes())
            .addCoCo(new UseOfValueLists())
            .addCoCo(new IdentifiersAreUnique())
            .addCoCo(new JavaPVariableIdentifiersUnique());
  }
}

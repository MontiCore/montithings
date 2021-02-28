package montithings.trafos;

import arcbasis._ast.*;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMACompilationUnitBuilder;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehaviorBuilder;
import montithings._ast.ASTMTComponentModifierBuilder;
import montithings._ast.ASTMTComponentTypeBuilder;
import montithings._auxiliary.ComfortableArcMillForMontiThings;
import montithings.util.TrafoUtil;

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
        ASTConnectorBuilder connectorBuilder = ComfortableArcMillForMontiThings.connectorBuilder();
        connectorBuilder.setSource(source);
        connectorBuilder.addTarget(target);
        comp.getComponentType().getBody().addArcElement(connectorBuilder.build());
    }

    /**
     * Searches and removes connection defined in the given component with the source port on the left hand side and
     * the target port at the right hand side. If the connection is not defined, nothing will be removed.
     *
     * @param comp   AST of component which is modified
     * @param source AST of source port
     * @param target AST of target port
     */
    protected void removeConnection(ASTMACompilationUnit comp, ASTPortAccess source, ASTPortAccess target) {
        Optional<ASTConnector> match =
                comp.getComponentType().getConnectors().stream()
                        .filter(conn -> conn.getTargetList().contains(target))
                        .filter(conn -> conn.getSource().equals(source))
                        .findFirst();
        match.ifPresent(astConnector -> comp.getComponentType().getBody().removeArcElement(astConnector));
    }


    /**
     * Add a port to the given component.
     *
     * @param comp       AST of component which is modified
     * @param name       Name of the port
     * @param isOutgoing Defines the direction of the port
     * @param type       Defines the type of the port
     */
    protected void addPort(ASTMACompilationUnit comp, String name, Boolean isOutgoing, ASTMCType type) {
        ASTPortDeclarationBuilder portDeclarationBuilder = ComfortableArcMillForMontiThings.portDeclarationBuilder();
        portDeclarationBuilder
                .setIncoming(!isOutgoing)
                .addPort(name)
                .setMCType(type);

        ASTComponentInterfaceBuilder astComponentInstanceBuilder = ComfortableArcMillForMontiThings.componentInterfaceBuilder();

        astComponentInstanceBuilder.addPortDeclaration(portDeclarationBuilder.build());
        ASTComponentInterface build = astComponentInstanceBuilder.build();
        comp.getComponentType().getBody().addArcElement(build);
    }

    /**
     * Creates and returns a new empty compilation unit.
     *
     * @param packageId Package which the new component should belong to
     * @param typeName  Name of the component type, e.g. Source
     * @return ASTMACompilationUnit of the newly created component
     */
    protected ASTMACompilationUnit createCompilationUnit(ASTMCQualifiedName packageId, String typeName) {
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
     * Adds an empty behavior java block to the given component.
     *
     * @param comp AST of component which is modified
     */
    protected void addEmptyBehavior(ASTMACompilationUnit comp) {
        ASTMCJavaBlock javaBlock = MontiThingsMill.mCJavaBlockBuilder().build();

        ASTBehaviorBuilder behavior = MontiThingsMill.behaviorBuilder();
        behavior.setMCJavaBlock(javaBlock);
        comp.getComponentType().getBody().addArcElement(behavior.build());
    }


    /**
     * @param comp         AST of component which is modified
     * @param typeName     Type name
     * @param instanceName Instance name
     */
    protected void addSubComponentInstantiation(ASTMACompilationUnit comp, String typeName, String instanceName) {
        ASTComponentInstantiationBuilder instantiationBuilder = ComfortableArcMillForMontiThings.componentInstantiationBuilder();
        instantiationBuilder.addInstance(instanceName);

        instantiationBuilder.setMCType(createCompilationUnitType(typeName));
        comp.getComponentType().getBody().addArcElement(instantiationBuilder.build());
    }

    /**
     * Searches and replaces the types of matching instantiations
     *
     * @param comp    AST of component which is modified
     * @param type    The type which should get replaced
     * @param newType The new type
     */
    protected void replaceComponentInstantiationType(ASTMACompilationUnit comp, String type, String newType) {
        for (ASTComponentInstantiation subComponentInstantiation : comp.getComponentType().getSubComponentInstantiations()) {
            String typeSubComp = printSimpleType(subComponentInstantiation.getMCType());
            if (type.equals(typeSubComp)) {
                subComponentInstantiation.setMCType(createCompilationUnitType(newType));
            }
        }
    }

    /**
     * Creates a ASTMCQualifiedType with the given name.
     *
     * @param name Name of the type
     * @return Corresponding ASTMCQualifiedType
     */
    private ASTMCQualifiedType createCompilationUnitType(String name) {
        ASTMCQualifiedName astmcQualifiedName = MontiThingsMill.mCQualifiedNameBuilder()
                .setPartsList(Collections.singletonList(name)).build();
        return MontiThingsMill.mCQualifiedTypeBuilder().
                setMCQualifiedName(astmcQualifiedName).build();
    }
}

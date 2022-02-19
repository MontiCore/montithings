// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._ast.ASTPortDirectionIn;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTMTComponentType;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import org.apache.commons.lang3.StringUtils;

/**
 * Helpers to create class diagrams in the scope for components
 */
public class ClassDiagramUtil {

  public static final String COMPONENT_TYPE_PREFIX = "Co";

  private static final MontiThingsTypeCheck tc = new MontiThingsTypeCheck(
          new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());

  public static CD4CodeArtifactScope createClassDiagram(ASTMACompilationUnit node) {
    return createClassDiagram((ASTMTComponentType) node.getComponentType());
  }

  protected static CD4CodeArtifactScope createClassDiagram(ASTMTComponentType comp) {
    String componentTypeName = COMPONENT_TYPE_PREFIX + comp.getName();
    ASTModifier publicModifier = CD4CodeMill.modifierBuilder().PUBLIC().build();
    ASTCDClass astcdClass = CD4CodeMill.cDClassBuilder().setName(componentTypeName)
            .setModifier(publicModifier).build();
    if (comp.isPresentMTImplements()) {
      ASTCDInterfaceUsageBuilder interfaceUsageBuilder = CD4CodeMill.cDInterfaceUsageBuilder();
      for (String name : comp.getMTImplements().getNameList()) {
        ASTMCObjectType interfaceType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName
                        (CD4CodeMill.mCQualifiedNameBuilder().addParts(COMPONENT_TYPE_PREFIX + name).build()).build();
        interfaceUsageBuilder.addInterface(interfaceType);
      }
      astcdClass.setCDInterfaceUsage(interfaceUsageBuilder.build());
    }
    if (comp.getHead().isPresentParent() && comp.getHead().getParent() instanceof ASTMCObjectType) {
      astcdClass.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass
              ((ASTMCObjectType) comp.getHead().getParent()).build());
    }
    for (ASTPortDeclaration astPortDeclaration : comp.getPortDeclarations()) {
      boolean incoming;
      incoming = astPortDeclaration.getPortDirection() instanceof ASTPortDirectionIn;
      for (ASTPort astPort : astPortDeclaration.getPortList()) {
        ASTMCType astmcType;
        ASTMCCustomTypeArgument typeArgument = CD4CodeMill.mCCustomTypeArgumentBuilder().
                setMCType(astPortDeclaration.getMCType()).build();
        if (typeArgument.getMCType() instanceof ASTSIUnitType4Computing) {
          typeArgument = CD4CodeMill.mCCustomTypeArgumentBuilder().
                  setMCType(((ASTSIUnitType4Computing) typeArgument.getMCType()).getMCPrimitiveType()).build();
        }
        if (typeArgument.getMCType() instanceof ASTSIUnitType) {
          //no explicit primitive type is given --> set to double
          typeArgument = CD4CodeMill.mCCustomTypeArgumentBuilder().
                  setMCType(CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build())
                  .build();
        }
        if (incoming) {
          astmcType = CD4CodeMill.mCBasicGenericTypeBuilder().addName("InPort").
                  addMCTypeArgument(typeArgument).build();
        } else {
          astmcType = CD4CodeMill.mCBasicGenericTypeBuilder().addName("OutPort").
                  addMCTypeArgument(typeArgument).build();
        }
        //add port attribute
        ASTCDAttribute attribute = CD4CodeMill.cDAttributeBuilder().setName(astPort.getName()).setInitialAbsent()
                .setModifier(publicModifier).setMCType(astmcType).build();
        astcdClass.addCDMember(attribute);

        //add port get-method
        ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder().setMCType(typeArgument.getMCType()).build();
        ASTCDMethod getMethod = CD4CodeMill.cDMethodBuilder().setMCReturnType(returnType).setModifier(publicModifier)
                .setName("get" + StringUtils.capitalize(astPort.getName())).build();
        astcdClass.addCDMember(getMethod);

        //add port isConnected-method
        ASTMCReturnType booleanReturnType = CD4CodeMill.mCReturnTypeBuilder().setMCType
                (CD4CodeMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build())
                .build();
        ASTCDMethod isConnectedMethod = CD4CodeMill.cDMethodBuilder().setMCReturnType(booleanReturnType)
                .setModifier(publicModifier).setName("isConnected" + StringUtils.capitalize(astPort.getName())).build();
        astcdClass.addCDMember(isConnectedMethod);
      }
    }
    ASTCDDefinition astcdDefinition = CD4CodeMill.cDDefinitionBuilder().setModifier(publicModifier).
            addCDElement(astcdClass).setName(componentTypeName).build();
    ASTCDCompilationUnit astcdCompilationUnit = CD4CodeMill.cDCompilationUnitBuilder().
            setCDDefinition(astcdDefinition).build();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    for (ASTCDMember astcdMember : astcdClass.getCDMemberList()) {
      if (astcdMember instanceof ASTCDAttribute) {
        setSymType((ASTCDAttribute) astcdMember);
      }
      else if (astcdMember instanceof ASTCDMethod) {
        setSymType((ASTCDMethod) astcdMember);
      }
    }
    astcdClass.getSymbol().setIsClass(true);
    return (CD4CodeArtifactScope) scope;
  }

  private static void setSymType(ASTCDMethod method) {
    method.getSymbol().
            setReturnType(getSymTypeFromMCType(method.getMCReturnType().getMCType(), method.getEnclosingScope()));
  }

  private static void setSymType(ASTCDAttribute attribute) {
    SymTypeOfGenerics symType;
    ASTMCType type = attribute.getMCType();
    if (type instanceof ASTMCBasicGenericType) {
      if (((ASTMCBasicGenericType) type).getMCTypeArgument(0) instanceof ASTMCCustomTypeArgument) {
        ASTMCType typeArgument = ((ASTMCCustomTypeArgument) ((ASTMCBasicGenericType) type).getMCTypeArgument(0)).getMCType();
        symType = SymTypeExpressionFactory.createGenerics(((ASTMCBasicGenericType) type).getName(0), attribute.getEnclosingScope());
        symType.addArgument(getSymTypeFromMCType(typeArgument, attribute.getEnclosingScope()));
        attribute.getSymbol().setType(symType);
      }
    }
  }

  protected static SymTypeExpression getSymTypeFromMCType(ASTMCType type, ICDBasisScope enclosingScope) {
    if (type instanceof ASTMCQualifiedType) {
      return SymTypeExpressionFactory.createTypeObject(
              ((ASTMCQualifiedType) type).getMCQualifiedName().getQName(), enclosingScope);
    }
    else if (type instanceof ASTSIUnitType) {
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType) type).accept(synthesizeSymTypeFromMontiThings.getTraverser());
      return synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else if (type instanceof ASTSIUnitType4Computing) {
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType4Computing) type).accept(synthesizeSymTypeFromMontiThings.getTraverser());
      return synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else if (type instanceof ASTMCObjectType) {
      return SymTypeExpressionFactory.createTypeObject(
              type.printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter())), MontiThingsMill.scope());
    }
    else {
      return tc.symTypeFromAST(type);
    }
  }
}

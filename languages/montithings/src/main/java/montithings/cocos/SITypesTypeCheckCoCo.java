package montithings.cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTPortDeclaration;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montithings._visitor.MontiThingsVisitor;
import montithings.types.check.DeriveSymTypeOfMontiThings;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

public class SITypesTypeCheckCoCo extends TypeCheckCoCo implements MontiArcASTMACompilationUnitCoCo, MontiThingsVisitor {
  /**
   * Creates an instance of TypeCheckCoCo
   *
   * @param typeCheck a {@link TypeCheck} object instantiated with the correct
   *                  ISynthesize and ITypesCalculator objects of
   *                  the current language
   */
  public SITypesTypeCheckCoCo(TypeCheck typeCheck) {
    super(typeCheck);
  }

  public static SITypesTypeCheckCoCo getCoCo() {
    TypeCheck typeCheck = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThings());
    return new SITypesTypeCheckCoCo(typeCheck);
  }

  @Override
  public void check(ASTMACompilationUnit astmaCompilationUnit) {
    astmaCompilationUnit.accept(this);
  }

  @Override
  public void visit(ASTArcParameter node){
    if(node.isPresentDefault()){
      checkFieldOrVariable(node, node.getDefault());
    }
  }

  @Override
  public void visit(ASTArcField node){
    checkFieldOrVariable(node, node.getInitial());
  }

  //TODO: PortDeclaration
}

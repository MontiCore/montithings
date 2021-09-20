package cd4montithings.trafos;

import cd4montithings._ast.ASTCDPort;
import cd4montithings._visitor.CD4MontiThingsTraverser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

public class GeneratePortSymTypeTrafo implements CD4MontiThingsTraverser {
  private TypeCheck tc;

  public ASTCDCompilationUnit transform(ASTCDCompilationUnit originalModel, TypeCheck typeCheck) {
    tc = typeCheck;
    originalModel.accept(this);
    return originalModel;
  }

  @Override
  public void visit(ASTCDPort cdPort) {
    SymTypeExpression symType;
    if (cdPort.getMCType() instanceof ASTMCQualifiedType) {
      symType = SymTypeExpressionFactory.createTypeObject(((ASTMCQualifiedType) cdPort.getMCType()).getMCQualifiedName().getQName(), cdPort.getEnclosingScope());
    }
    else {
      symType = tc.symTypeFromAST(cdPort.getMCType());
    }
    cdPort.setType(symType);
    cdPort.getSymbol().setType(symType);
  }
}

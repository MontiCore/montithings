package montithings._lsp.language_access;

import montiarc._ast.ASTMACompilationUnit;
import montithings._parser.MontiThingsAntlrParser;
import montithings.cocos.MontiThingsCoCos;

public class MontiThingsLspCoCoRunner extends MontiThingsLspCoCoRunnerTOP{

    @Override
    public boolean needsSymbols() {
        return true;
    }

    @Override
    public void runAllCoCos(ASTMACompilationUnit ast) {
        MontiThingsCoCos.createChecker().checkAll(ast);
    }
}

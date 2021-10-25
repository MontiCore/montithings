package montithings._lsp.language_access;

import de.monticore.io.paths.ModelPath;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsArtifactScope;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.MontiThingsGlobalScope;

import java.nio.file.Path;

public class MontiThingsScopeManager extends MontiThingsScopeManagerTOP {
    private MontiThingsTool tool = new MontiThingsTool();

    // TODO: this is equivalent to indexing => remove auto indexing?
    @Override
    public void initGlobalScope(ModelPath modelPath) {
        montithings.MontiThingsMill.init();
        // TODO: MontiThingsTool should expose createMTGlobalScope(ModelPath)
        Path[] paths = modelPath.getFullPathOfEntries().toArray(new Path[0]);
        IMontiThingsGlobalScope gs = tool.createMTGlobalScope(paths);
        // TODO: cast, does ILangGlobalScope suffice in template?
        setGlobalScope((MontiThingsGlobalScope) gs);
    }

    @Override
    public IMontiThingsArtifactScope createSymbolTable(ASTMACompilationUnit ast) {
        // TODO: remove old version of the artifact scope?
        return (IMontiThingsArtifactScope) tool.createSymbolTable(ast);
    }
}

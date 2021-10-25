package montithings._lsp;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.SemanticTokensProvider;
import de.mclsg.lsp.features.impl.DelegatingSemanticsTokenProvider;
import montithings._lsp.language_access.MontiThingsLanguageAccess;
import montithings._lsp.syntax_highlighting.NoKeywordAsKeywordTokenRule;
import org.eclipse.lsp4j.services.LanguageClient;

public class MontiThingsTextDocumentService extends MontiThingsTextDocumentServiceTOP{
    public MontiThingsTextDocumentService(DocumentManager documentManager, LanguageClient languageClient, MontiThingsLanguageAccess languageAccess) {
        super(documentManager, languageClient, languageAccess);
    }

    @Override
    protected SemanticTokensProvider initializeSemanticTokensProvider() {
        DelegatingSemanticsTokenProvider res = new DelegatingSemanticsTokenProvider(new MontiThingsSyntaxHighlightingService(languageAccess));
        res.getTokenTypeRules().add(new NoKeywordAsKeywordTokenRule());
        return res;
    }
}

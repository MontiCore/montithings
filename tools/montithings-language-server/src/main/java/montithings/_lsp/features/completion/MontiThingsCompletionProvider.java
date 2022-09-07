// (c) https://github.com/MontiCore/monticore
package montithings._lsp.features.completion;

import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.completion.ExpectedToken;
import de.mclsg.lsp.features.completion.strategy.CompletionStrategy;
import de.monticore.symboltable.ISymbol;
import montithings._lsp.language_access.MontiThingsLanguageAccess;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MontiThingsCompletionProvider extends MontiThingsCompletionProviderTOP{

    public MontiThingsCompletionProvider(DocumentManager documentManager, MontiThingsLanguageAccess languageAccess) {
        super(documentManager, languageAccess);

        completionStrategyManager.registerCompletionStrategy(new CompletionStrategy() {
            @Override
            public List<? extends ISymbol> getSymbols(ExpectedToken expectedToken, DocumentInformation documentInformation) {
                return new ArrayList<>();
            }

            @Override
            public List<CompletionItem> getAdditionalCompletions(ExpectedToken expectedToken, DocumentInformation documentInformation, String contentUntilCompletion) {
                // TODO: this might be unstable, as it depends on the naming convention of keywords marked with nokeyword
                List<String> tokenPathElements = expectedToken.tokenPath.tokenPathElements;
                String last = tokenPathElements.get(tokenPathElements.size() - 1);
                String tmp = last.substring("nokeyword_".length());
                String res = tmp.replaceAll("\\d", "");
                CompletionItem completionItem = new CompletionItem(res);
                completionItem.setKind(CompletionItemKind.Keyword);
                return Collections.singletonList(completionItem);
            }

            @Override
            public boolean matches(ExpectedToken expectedToken) {
                List<String> tokenPathElements = expectedToken.tokenPath.tokenPathElements;
                String last = tokenPathElements.get(tokenPathElements.size() - 1);
                return last.startsWith("nokeyword_");
            }
        });
    }
}

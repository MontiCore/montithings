package montithings._lsp.syntax_highlighting;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.impl.TokenTypeRule;
import org.eclipse.lsp4j.SemanticTokenTypes;

import java.util.Optional;

public class NoKeywordAsKeywordTokenRule implements TokenTypeRule {

        @Override
        public Optional<String> apply(Token token) {
            return Optional.of(SemanticTokenTypes.Keyword);
        }

        @Override
        public boolean matches(Token token) {
            return token
                    .getMatchedToken()
                    .filter(mt -> mt.tokenPath.getTokenPath().contains("nokeyword")).isPresent();
        }
    }

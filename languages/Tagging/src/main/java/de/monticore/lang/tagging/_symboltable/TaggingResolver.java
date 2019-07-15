/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.tagging._symboltable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.monticore.ast.ASTNode;
import de.monticore.lang.tagging._ast.*;
import de.monticore.lang.tagging._parser.TaggingParser;
import de.monticore.lang.tagging.helper.RangeFixer;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.SymbolPredicate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingFilter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

/**
 * Created by MichaelvonWenckstern on 16.11.2017.
 */
public class TaggingResolver implements Scope {
  public static final String TAG_FILE_ENDING = "tag";
  public static TaggingResolver currentTaggingResolver; // will be set by symbol table creator

  private Collection<String> modelPaths;
  private TaggingResolver taggingResolver;
  private Scope globalScope;
  private boolean tagModelsLoaded = false;
  private Set<TagSymbolCreator> tagSymbolCreators = new LinkedHashSet<>();
  private Set<ResolvingFilter> tagSymbolResolvingFilters = new LinkedHashSet<>();
  // create for each symbol its own scope where all the tags for this symbol are stored
  private HashMap<Symbol, Scope> tagSymbolRepository = new LinkedHashMap<>();
  public TaggingResolver(Scope globalScope, Collection<String> modelPaths) {
    if (globalScope instanceof TaggingResolver) {
      taggingResolver = (TaggingResolver)globalScope; // use the already existing tagging resolver
      taggingResolver.modelPaths.addAll(modelPaths);
    } else {                                                                                // to allow chaining of tagging schemas
      taggingResolver = this;
      this.globalScope = globalScope;
      this.modelPaths = modelPaths;
    }
  }

  public TaggingResolver(Scope globalScope, List<File> modelPaths){

    this(globalScope, modelPaths.stream().map(x -> x.getPath()).collect(Collectors.toList()));
  }
  // implements all Scope methods by delegating it to globalScope to make reusage easier

  public void addTagSymbolCreator(TagSymbolCreator tagSymbolCreator) {
    taggingResolver.tagSymbolCreators.add(tagSymbolCreator);
  }
  public void addTagSymbolResolvingFilter(ResolvingFilter resolvingFilter) {
    taggingResolver.tagSymbolResolvingFilters.add(resolvingFilter);
  }

  public Collection<TagSymbol> getTags(Symbol symbol, TagKind tagKind) {
    if (!taggingResolver.tagModelsLoaded) {
      taggingResolver.loadTagModels();
      taggingResolver.tagModelsLoaded = true;
    }
    Scope s = taggingResolver.tagSymbolRepository.get(symbol);
    if (s == null) return Collections.emptyList();
    return s.resolveLocally(tagKind);
  }

  private void loadTagModels() {
    // parse all tag files in the model paths and
    Collection<ASTTaggingUnit> tags = taggingResolver.parseTags(taggingResolver.modelPaths);
    for (ASTTaggingUnit tag : tags) {
      // for each tag file apply all registered tagSymbolCreators
      taggingResolver.applyTagSymbolCreators(tag);
    }

  }

  private void applyTagSymbolCreators(ASTTaggingUnit tag) {
    for (TagSymbolCreator tsc : taggingResolver.tagSymbolCreators) {
      tsc.create(tag, taggingResolver);
    }
  }

  protected Collection<ASTTaggingUnit> parseTags(final Collection<String> modelPaths) {
    final Collection<ASTTaggingUnit> foundModels = new ArrayList<>();
    for (String mp : modelPaths) {
      final Path completePath = Paths.get(mp);
      final File f = completePath.toFile();
      if (f != null && f.isDirectory()) {
        Collection<File> tagFiles = FileUtils.listFiles(f, new String[] {TAG_FILE_ENDING}, true);

        tagFiles.stream().forEachOrdered(t -> {
          final TaggingParser parser = new TaggingParser();
          Optional<ASTTaggingUnit> ast = Optional.empty();
          try {
            ast = parser.parse(t.getAbsolutePath());
          }
          catch (IOException e) {
            Log.error("could not open file " + t, e);
          }
          if (ast.isPresent()) {
            // todo check package conformity
            RangeFixer.fixTaggingUnit(ast.get());
            foundModels.add(ast.get());
          }
        });
      }
    }
    return foundModels;
  }


  public void addTag(Symbol symbol, TagSymbol tagDefinition) {
    // add the tags to the scope belonging to the symbol in tagSymbolRepository
    // when the symbol has no scope yet, create a new scope and register all resolving filters
    Scope scope = taggingResolver.tagSymbolRepository.get(symbol);
    if (scope == null) {
      scope = new CommonScope();
      addResolvingFilters(scope);
      taggingResolver.tagSymbolRepository.put(symbol, scope);
    }
    scope.getAsMutableScope().add(tagDefinition);
  }

  protected void addResolvingFilters(final Scope scope) {
    taggingResolver.tagSymbolResolvingFilters.forEach(filter -> scope.getAsMutableScope().addResolver(filter));
  }

  @Override
  public Optional<String> getName() {
    return globalScope.getName();
  }

  @Override
  public Optional<? extends Scope> getEnclosingScope() {
    return globalScope.getEnclosingScope();
  }

  @Override
  public List<? extends Scope> getSubScopes() {
    return globalScope.getSubScopes();
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String s, SymbolKind symbolKind) {
    return globalScope.resolve(s, symbolKind);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String s, SymbolKind symbolKind, AccessModifier accessModifier) {
    return globalScope.resolve(s, symbolKind, accessModifier);
  }

  @Override
  public <T extends Symbol> Optional<T> resolve(String s, SymbolKind symbolKind, AccessModifier accessModifier, Predicate<Symbol> predicate) {
    return globalScope.resolve(s, symbolKind, accessModifier, predicate);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveImported(String s, SymbolKind symbolKind, AccessModifier accessModifier) {
    return globalScope.resolveImported(s, symbolKind, accessModifier);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String s, SymbolKind symbolKind) {
    return globalScope.resolveMany(s, symbolKind);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String s, SymbolKind symbolKind, AccessModifier accessModifier) {
    return globalScope.resolveMany(s, symbolKind, accessModifier);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String s, SymbolKind symbolKind, Predicate<Symbol> predicate) {
    return globalScope.resolveMany(s, symbolKind, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(String s, SymbolKind symbolKind, AccessModifier accessModifier, Predicate<Symbol> predicate) {
    return globalScope.resolveMany(s, symbolKind, accessModifier, predicate);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveLocally(String s, SymbolKind symbolKind) {
    return globalScope.resolveLocally(s, symbolKind);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveLocally(SymbolKind symbolKind) {
    return globalScope.resolveLocally(symbolKind);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String s, SymbolKind symbolKind) {
    return globalScope.resolveDown(s, symbolKind);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String s, SymbolKind symbolKind, AccessModifier accessModifier) {
    return globalScope.resolveDown(s, symbolKind, accessModifier);
  }

  @Override
  public <T extends Symbol> Optional<T> resolveDown(String s, SymbolKind symbolKind, AccessModifier accessModifier, Predicate<Symbol> predicate) {
    return globalScope.resolveDown(s, symbolKind, accessModifier, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String s, SymbolKind symbolKind) {
    return globalScope.resolveDownMany(s, symbolKind);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String s, SymbolKind symbolKind, AccessModifier accessModifier) {
    return globalScope.resolveDownMany(s, symbolKind, accessModifier);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(String s, SymbolKind symbolKind, AccessModifier accessModifier, Predicate<Symbol> predicate) {
    return globalScope.resolveDownMany(s, symbolKind, accessModifier, predicate);
  }

  @Override
  public Map<String, Collection<Symbol>> getLocalSymbols() {
    return globalScope.getLocalSymbols();
  }

  @Override
  public int getSymbolsSize() {
    return globalScope.getSymbolsSize();
  }

  @Override
  public boolean isShadowingScope() {
    return globalScope.isShadowingScope();
  }

  @Override
  public boolean isSpannedBySymbol() {
    return globalScope.isSpannedBySymbol();
  }

  @Override
  public Optional<? extends ScopeSpanningSymbol> getSpanningSymbol() {
    return globalScope.getSpanningSymbol();
  }

  @Override
  public boolean exportsSymbols() {
    return globalScope.exportsSymbols();
  }

  @Override
  public Set<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return globalScope.getResolvingFilters();
  }

  @Override
  public Optional<? extends ASTNode> getAstNode() {
    return globalScope.getAstNode();
  }

  @Override
  public MutableScope getAsMutableScope() {
    return globalScope.getAsMutableScope();
  }
}

/* generated by template templates.de.monticore.lang.tagschema.TagSchema*/


package nfp.LatencyTagSchema;

import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

/**
 * generated by TagSchema.ftl
 */
public class LatencyTagSchema {

  protected static LatencyTagSchema instance = null;

  protected LatencyTagSchema() {}

  protected static LatencyTagSchema getInstance() {
    if (instance == null) {
      instance = new LatencyTagSchema();
    }
    return instance;
  }

  protected void doRegisterTagTypes(TaggingResolver tagging) {
    tagging.addTagSymbolCreator(new LatencyCmpInstSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(LatencyCmpInstSymbol.KIND));
    tagging.addTagSymbolCreator(new LatencyCmpSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(LatencyCmpSymbol.KIND));
    tagging.addTagSymbolCreator(new LatencyConnSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(LatencyConnSymbol.KIND));
    tagging.addTagSymbolCreator(new LatencyPortSymbolCreator());
    tagging.addTagSymbolResolvingFilter(CommonResolvingFilter.create(LatencyPortSymbol.KIND));
  }

  public static void registerTagTypes(TaggingResolver tagging) {
    getInstance().doRegisterTagTypes(tagging);
  }

}
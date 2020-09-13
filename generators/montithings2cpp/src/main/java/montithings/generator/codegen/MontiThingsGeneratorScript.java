// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import montithings.generator.MontiThingsGeneratorTool;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;

/**
 * TODO
 *
 * @author (last commit) JFuerste
 */

public class MontiThingsGeneratorScript extends Script implements GroovyRunner {

  protected static final String[] DEFAULT_IMPORTS = {};

  protected static final String LOG = "MontiThingsGeneratorScript";

  /**
   * @see de.se_rwth.commons.groovy.GroovyRunner#run(java.lang.String,
   * de.se_rwth.commons.configuration.Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(MontiThingsGeneratorScript.class)
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));

    // configuration
    MontiThingsConfiguration config = MontiThingsConfiguration.withConfiguration(configuration);

    // we add the configuration object as property with a special property name
    builder.addVariable(MontiThingsConfiguration.CONFIGURATION_PROPERTY, config);

    config.getAllValues().forEach(builder::addVariable);

    // after adding everything we override a couple of known variable
    // bindings to have them properly typed in the script
    builder.addVariable(MontiThingsConfiguration.Options.MODELPATH.toString(), config.getModelPath());
    builder.addVariable(MontiThingsConfiguration.Options.TESTPATH.toString(), config.getTestPath());
    builder.addVariable(MontiThingsConfiguration.Options.OUT.toString(), config.getOut());
    builder.addVariable(MontiThingsConfiguration.Options.HANDWRITTENCODEPATH.toString(), config.getHWCPath());
    builder.addVariable(MontiThingsConfiguration.Options.PLATFORM.toString(), config.getPlatform());

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetFilepath}
   */
  public void generate(File modelPath, File targetFilepath, File hwcPath, File testPath, ConfigParams configParams) {
    new MontiThingsGeneratorTool().generate(modelPath, targetFilepath, hwcPath, testPath, configParams);
  }

  // #######################
  // log functions
  // #######################

  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG);
  }

  public void debug(String msg) {
    Log.debug(msg, LOG);
  }

  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG);
  }

  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG);
  }

  public void info(String msg) {
    Log.info(msg, LOG);
  }

  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG);
  }

  public void warn(String msg) {
    Log.warn(msg);
  }

  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }

  public void error(String msg) {
    Log.error(msg);
  }

  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }

  /**
   * @see groovy.lang.Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
}

// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.generator.script;

import de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import montithings.generator.config.Options;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;

public class SD4CGeneratorScript extends Script implements GroovyRunner {

  protected static final String[] DEFAULT_IMPORTS = {};

  protected static final String LOG = "MontiThingsGeneratorScript";

  /**
   * @see GroovyRunner#run(String,
   * Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
        .withScriptBaseClass(SD4CGeneratorScript.class)
        .withClassLoader(getClass().getClassLoader())
        .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));

    // configuration
    SD4CConfiguration config = SD4CConfiguration.withConfiguration(configuration);

    // we add the configuration object as property with a special property name
    builder.addVariable(SD4CConfiguration.CONFIGURATION_PROPERTY, config);

    config.getAllValues().forEach(builder::addVariable);

    // after adding everything we override a couple of known variable
    // bindings to have them properly typed in the script
    builder.addVariable(Options.MODELPATH.toString(), config.getModelPath());
    builder.addVariable(Options.TESTPATH.toString(), config.getTestPath());
    builder.addVariable(Options.OUT.toString(), config.getOut());

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetPath}
   */
  public void generate(File modelPath, File testPath, File targetPath) {
    SD4ComponentTestingTool tool = new SD4ComponentTestingTool();
    // TODO: Pretty Print als ARC

    // TODO: For Schleife für SD4C Dateien

    tool.generate("TODO", "TODO", "TODO");
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
   * @see Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
}

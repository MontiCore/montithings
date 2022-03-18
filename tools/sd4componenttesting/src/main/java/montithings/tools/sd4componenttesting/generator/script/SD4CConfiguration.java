// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting.generator.script;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.Options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SD4CConfiguration implements Configuration {
  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  public static final String DEFAULT_HWC_DIRECTORY = "src";

  /**
   * Object that is should be used by the Generator to retrieve its configuration.
   */
  public static final ConfigParams configParams = new ConfigParams();

  protected final Configuration configuration;

  /**
   * Factory method for TemplateClassGeneratorConfiguration.
   */
  public static SD4CConfiguration withConfiguration(Configuration configuration) {
    return new SD4CConfiguration(configuration);
  }

  /**
   * Constructor for TemplateClassGeneratorConfiguration
   */
  protected SD4CConfiguration(Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder.newChain()
      .add(DelegatingConfigurationContributor.with(internal)).build();
  }

  /**
   * @see Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see Configuration#getAsBoolean(String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see Configuration#getAsBooleans(String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see Configuration#getAsDouble(String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see Configuration#getAsDoubles(String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see Configuration#getAsInteger(String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see Configuration#getAsIntegers(String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see Configuration#getAsString(String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see Configuration#getAsStrings(String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see Configuration#getValue(String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see Configuration#getValues(String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }

  public File getModelPath() {
    Optional<String> modelPath = getAsString(Options.MODELPATH);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    modelPath = getAsString(Options.MODELPATH_SHORT);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    return null;
  }

  public File getTestPath() {
    Optional<String> testPath = getAsString(Options.TESTPATH);
    if (testPath.isPresent()) {
      Path mp = Paths.get(testPath.get());
      return mp.toFile();
    }
    else if (getModelPath() != null) {
      Path defaultTestPath = getModelPath().toPath();
      for (int i = 0; i < 3; i++) {
        defaultTestPath = defaultTestPath.getParent();
        if (defaultTestPath == null) {
          return null;
        }
      }
      if (Paths.get(defaultTestPath.toString(), "test", "resources", "gtests").toFile()
        .isDirectory()) {
        return Paths.get(defaultTestPath.toString(), "test", "resources", "gtests").toFile();
      }
    }
    return new File("");
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "out".
   *
   * @return output directory file
   */
  public File getOut() {
    Optional<String> out = getAsString(Options.OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.OUT_MONTICORE);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.OUT_SHORT);
    // fallback default is "out"
    return out.map(File::new).orElseGet(() -> new File(DEFAULT_OUTPUT_DIRECTORY));
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().map(File::new).collect(Collectors.toList());
  }

  /**
   * @see Configuration#hasProperty(String)
   */
  @Override
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }
}

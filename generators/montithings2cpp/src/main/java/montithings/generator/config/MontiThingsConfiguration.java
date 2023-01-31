// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import de.se_rwth.commons.logging.Log;
import montithings.util.MontiThingsError;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MontiThingsConfiguration implements Configuration {
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
  public static MontiThingsConfiguration withConfiguration(Configuration configuration) {
    return new MontiThingsConfiguration(configuration);
  }

  /**
   * Constructor for TemplateClassGeneratorConfiguration
   */
  protected MontiThingsConfiguration(Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder.newChain()
      .add(DelegatingConfigurationContributor.with(internal)).build();
    configParams.setTargetPlatform(getPlatform());
    configParams.setSplittingMode(getSplittingMode());
    configParams.setLogTracing(getLogTracing());
    configParams.setRecordingMode(getRecordingMode());
    configParams.setPortNameTrafo(getPortNameTrafo());
    configParams.setHwcTemplatePath(Paths.get(getHWCPath().getAbsolutePath()));
    configParams.setMessageBroker(getMessageBroker(getSplittingMode()));
    configParams.setReplayMode(getReplayMode());
    configParams.setReplayDataFile(getReplayDataFile());
    configParams.setHwcPath(getHWCPath());
    configParams.setLanguagePath(getLanguagePath());
    configParams.setProjectVersion(getVersion());
    configParams.setMainComponent(getMainComponent());
    configParams.setSerializationMode(getSerializationMode());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
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

  public File getLanguagePath() {
    Optional<String> languagePath = getAsString(Options.LANGUAGEPATH);
    if (languagePath.isPresent()) {
      Path mp = Paths.get(languagePath.get());
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
      for (int i = 0; i < 4; i++) {
        defaultTestPath = defaultTestPath.getParent();
        if (defaultTestPath == null) {
          Log.error("default test path could not be found");
        }
      }
      if (Paths.get(defaultTestPath.toString(), "target", "sd4c", "cpp").toFile()
        .isDirectory()) {
        return Paths.get(defaultTestPath.toString(), "target", "sd4c", "cpp").toFile();
      }
    }
    return new File("");
  }

  public File getHWCPath() {
    Optional<String> hwcPath = getAsString(Options.HANDWRITTENCODEPATH);
    if (hwcPath.isPresent()) {
      Path hwc = Paths.get(hwcPath.get());
      return hwc.toFile();
    }
    hwcPath = getAsString(Options.HANDWRITTENCODEPATH_MONTICORE);
    if (hwcPath.isPresent()) {
      Path hwc = Paths.get(hwcPath.get());
      return hwc.toFile();
    }
    hwcPath = getAsString(Options.HANDWRITTENCODEPATH_SHORT);
    if (hwcPath.isPresent()) {
      Path hwc = Paths.get(hwcPath.get());
      return hwc.toFile();
    }
    return Paths.get(DEFAULT_HWC_DIRECTORY).toFile();
  }

  public String getVersion() {
    return getAsString(Options.VERSION).orElse("unspecified");
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

  public TargetPlatform getPlatform() {
    Optional<String> platform = getAsString(Options.PLATFORM);
    if (platform.isPresent()) {
      return TargetPlatform.fromString(platform.get());
    }
    // fallback default is "generic"
    return TargetPlatform.GENERIC;
  }

  public SplittingMode getSplittingMode() {
    Optional<String> splittingMode = getAsString(Options.SPLITTING);
    if (splittingMode.isPresent()) {
      return SplittingMode.fromString(splittingMode.get());
    }
    // fallback default is "off"
    return SplittingMode.OFF;
  }

  public SerializationMode getSerializationMode() {
    Optional<String> serializationMode = getAsString(Options.SERIALIZATION);
    if (serializationMode.isPresent()) {
      return SerializationMode.fromString(serializationMode.get());
    }
    // fallback default is "JSON"
    return SerializationMode.JSON;
  }

  public LogTracing getLogTracing() {
    Optional<String> logTracing = getAsString(Options.LOGTRACING);
    if (logTracing.isPresent()) {
      return LogTracing.fromString(logTracing.get());
    }
    // fallback default is "off"
    return LogTracing.OFF;
  }

  public MessageBroker getMessageBroker(SplittingMode splittingMode) {
    Optional<String> messageBroker = getAsString(Options.MESSAGEBROKER);
    if (!messageBroker.isPresent()) {
      messageBroker = getAsString(Options.MESSAGEBROKER_SHORT);
    }
    if (messageBroker.isPresent()) {
      return MessageBroker.fromString(messageBroker.get());
    }

    if (splittingMode == SplittingMode.OFF) {
      // fallback default if not splitting is disabled is "off"
      return MessageBroker.OFF;
    }
    else {
      // fallback default if splitted is enabled "MQTT"
      return MessageBroker.MQTT;
    }
  }

  public ReplayMode getReplayMode() {
    Optional<String> replayMode = getAsString(Options.REPLAYMODE);
    if (replayMode.isPresent()) {
      return ReplayMode.fromString(replayMode.get());
    }
    // fallback default is "off"
    return ReplayMode.OFF;
  }

  public File getReplayDataFile() {
    Optional<String> path = getAsString(Options.REPLAYDATAFILE);

    if (configParams.getReplayMode() == ReplayMode.ON && !path.isPresent()) {
      Log.error(MontiThingsError.GENERATOR_REPLAYDATA_REQUIRED.toString());
    }

    return path.map(File::new).orElseGet(() -> new File(path.orElse("")));
  }

  public String getMainComponent() {
    Optional<String> mainComp = getAsString(Options.MAINCOMP);
    Optional<String> mainCompShort = getAsString(Options.MAINCOMP_SHORT);

    if (mainComp.isPresent() && mainCompShort.isPresent()) {
      Log.error(String.format(MontiThingsError.GENERATOR_ONLY_ONE_MAIN.toString(),
        mainComp.get(), mainCompShort.get()));
    }
    if (configParams.getSplittingMode() == SplittingMode.OFF
      && !mainComp.isPresent() && !mainCompShort.isPresent()) {
      Log.error(MontiThingsError.GENERATOR_MAIN_REQUIRED.toString());
    }

    return mainComp.orElseGet(mainCompShort::get);
  }

  public RecordingMode getRecordingMode() {
    Optional<String> recordingMode = getAsString(Options.RECORDING);
    if (recordingMode.isPresent()) {
      return RecordingMode.fromString(recordingMode.get());
    }
    // fallback default is "off"
    return RecordingMode.OFF;
  }

  public PortNameTrafo getPortNameTrafo() {
    Optional<String> portNameTrafo = getAsString(Options.PORTNAME);
    if (portNameTrafo.isPresent()) {
      return PortNameTrafo.fromString(portNameTrafo.get());
    }
    // fallback default is "off"
    return PortNameTrafo.OFF;
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().map(File::new).collect(Collectors.toList());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#hasProperty(java.lang.String)
   */
  @Override
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }
}

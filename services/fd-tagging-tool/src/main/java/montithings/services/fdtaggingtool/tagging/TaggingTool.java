// (c) https://github.com/MontiCore/monticore
package montithings.services.fdtaggingtool.tagging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featurediagram.FeatureDiagramCLI;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import mcfdtool.FACT;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.services.fdtaggingtool.tagging._ast.ASTTag;
import montithings.services.fdtaggingtool.tagging._ast.ASTTagging;
import montithings.services.fdtaggingtool.tagging._cocos.TaggingCoCoChecker;
import montithings.services.fdtaggingtool.tagging._parser.TaggingParser;
import montithings.services.fdtaggingtool.tagging._symboltable.ITaggingGlobalScope;
import montithings.services.fdtaggingtool.tagging._symboltable.TaggingArtifactScope;
import montithings.services.fdtaggingtool.tagging._symboltable.TaggingSymbol;
import montithings.services.fdtaggingtool.tagging.cocos.FirstNameIsAFeature;
import montithings.services.fdtaggingtool.tagging.cocos.NoComponentIsMentionedTwiceInASingleTag;
import montithings.services.fdtaggingtool.tagging.cocos.NoTagIsMentionedTwice;
import montithings.services.fdtaggingtool.tagging.cocos.SecondNameIsAComponent;
import mtconfig.MTConfigTool;
import org.antlr.v4.runtime.RecognitionException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaggingTool {

  protected ITaggingGlobalScope tgGS;

  protected IMontiThingsGlobalScope mtGS;

  protected IFeatureDiagramGlobalScope fdGS;

  protected MontiThingsTool mtTool = new MontiThingsTool();

  protected FeatureDiagramCLI fdCLI = new FeatureDiagramCLI();

  protected FACT factTool = new FACT();

  protected MTConfigTool mtConfigTool = new MTConfigTool();

  protected String iotManagerURL = "http://localhost:4210/";

  protected ModelPath getModelPathFromModel(String modelFile) {
    Path directory = Paths.get(modelFile).getParent();
    return new ModelPath(directory);
  }

  public void terminate() {
    tgGS = null;
    mtGS = null;
    fdGS = null;
    mtTool = null;
    fdCLI = null;
    factTool = null;
    mtConfigTool = null;
  }

  public String getIotManagerURL() {
    return iotManagerURL;
  }

  public void setIotManagerURL(String url) {
    this.iotManagerURL = url;
  }

  public static ASTTagging parse(String model) {
    try {
      TaggingParser parser = new TaggingParser();
      Optional<ASTTagging> optTagging = parser.parse(model);

      if (!parser.hasErrors() && optTagging.isPresent()) {
        return optTagging.get();
      }
      Log.error("Tagging model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    return null;
  }

  public ITaggingGlobalScope initTaggingGlobalScope(ModelPath modelPath) {
    ITaggingGlobalScope globalScope = TaggingMill.globalScope();
    globalScope.clear();
    globalScope.setModelPath(modelPath);
    globalScope.setFileExt("tg");
    return globalScope;
  }

  public ASTTagging loadModel(String modelFile) {
    //Main function that loads the tagging model and sets up the other two scopes
    if (tgGS != null) {
      //Check if model is already loaded to skip unnecessary loading
      List<TaggingArtifactScope> scopesList = (List<TaggingArtifactScope>) tgGS.getSubScopes();
      String file = Paths.get(modelFile).getFileName().toString();
      for (TaggingArtifactScope scope : scopesList) {
        Optional<TaggingSymbol> taggingSymbol = scope.resolveTagging(file);
        if (taggingSymbol.isPresent()) {
          return taggingSymbol.get().getAstNode();
        }
      }
    }
    setUpScopes(modelFile);
    ASTTagging ast = parse(modelFile + ".tg");
    TaggingMill.scopesGenitorDelegator().createFromAST(ast);

    //Check cocos
    TaggingCoCoChecker customCoCos = new TaggingCoCoChecker();
    customCoCos.addCoCo(new FirstNameIsAFeature());
    customCoCos.addCoCo(new SecondNameIsAComponent());
    customCoCos.addCoCo(new NoTagIsMentionedTwice());
    customCoCos.addCoCo(new NoComponentIsMentionedTwiceInASingleTag());
    customCoCos.checkAll(ast);
    return ast;
  }

  public void setUpScopes(String modelsDirectory) {
    //Extract model path from directory
    ModelPath modelPath = getModelPathFromModel(modelsDirectory);

    //Set up the scopes
    tgGS = initTaggingGlobalScope(modelPath);
    mtGS = mtTool.processModels(modelPath);

    //Extra steps needed for the feature diagram scope
    fdGS = FeatureDiagramMill.globalScope();
    fdGS.setModelPath(tgGS.getModelPath());
    FeatureDiagramParser fdParser = new FeatureDiagramParser();
    fdCLI.createSymbolTable(fdCLI.parse(modelsDirectory + ".fd", fdParser));
  }

  /*--------------------------------------------------------------------------------------------------------------------------
  --------------------------------------------------------------------------------------------------------------------------*/
  public List<ASTFeatureConfiguration> findMaximalConfigurations(String modelsDirectory) {
    loadModel(modelsDirectory);
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();

    //load feature diagram from global scope
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes()
      .get(0).getAstNode();

    //if feature diagram is void don't bother continuing
    if (factTool.execIsVoidFeatureModel(featureDiagram)) {
      Log.warn(String.format("The feature diagram '%s% does not have valid configurations",
        featureDiagram.getName()));
      return outputList;
    }

    //search for biggest number of elements
    List<ASTFeatureConfiguration> configurations = factTool.execAllProducts(featureDiagram);
    ASTSelect selectedFeatures;
    int maxSize = 0;
    for (ASTFeatureConfiguration candidate : configurations) {
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() > maxSize) {
        maxSize = selectedFeatures.getNameList().size();
      }
    }
    for (ASTFeatureConfiguration candidate : configurations) {
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() == maxSize) {
        outputList.add(candidate);
      }
    }
    return outputList;
  }

  public List<ASTFeatureConfiguration> findMinimalConfigurations(String modelsDirectory) {
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();

    //load feature diagram from global scope
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes()
      .get(0).getAstNode();

    //if feature diagram is void don't bother continuing
    if (factTool.execIsVoidFeatureModel(featureDiagram)) {
      Log.warn(String.format("The feature diagram '%s% does not have valid configurations",
        featureDiagram.getName()));
      return outputList;
    }

    //search for biggest number of elements
    List<ASTFeatureConfiguration> configurations = factTool.execAllProducts(featureDiagram);
    ASTSelect selectedFeatures;
    int minSize = Integer.MAX_VALUE;
    for (ASTFeatureConfiguration candidate : configurations) {
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() < minSize) {
        minSize = selectedFeatures.getNameList().size();
      }
    }
    for (ASTFeatureConfiguration candidate : configurations) {
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() == minSize) {
        outputList.add(candidate);
      }
    }
    return outputList;
  }

  public List<ASTFeatureConfiguration> findMinimalPotentialConfiguration(String modelsDirectory,
    List<ASTMCQualifiedName> activatedComponents) {
    List<ASTFeatureConfiguration> minimalConfigurations = findMinimalConfigurations(
      modelsDirectory);
    List<ASTFeatureConfiguration> minimalPotentialConfigurations = new ArrayList<>();
    ASTSelect features;
    List<String> featureNames;
    List<ASTMCQualifiedName> activatedFeatures = findPotentialFeatures(modelsDirectory,
      activatedComponents);
    int foundFeatures = 0;

    for (ASTFeatureConfiguration configuration : minimalConfigurations) {
      features = (ASTSelect) configuration.getFCElement(0);
      featureNames = features.getNameList();
      for (String name : featureNames) {
        if (activatedFeatures.stream().filter(o -> o.getBaseName().equals(name)).findFirst()
          .isPresent())
          foundFeatures++;
      }
      if (foundFeatures == featureNames.size())
        minimalPotentialConfigurations.add(configuration);
      foundFeatures = 0;
    }
    return minimalPotentialConfigurations;
  }

  public List<ASTFeatureConfiguration> findConfigurationOfSize(
    List<ASTFeatureConfiguration> configurations, int size) {
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();
    for (ASTFeatureConfiguration configuration : configurations) {
      if (((ASTSelect) configuration.getFCElement(0)).sizeNames() == size) {
        outputList.add(configuration);
      }
    }
    return outputList;
  }

  public List<ASTFeatureConfiguration> findMaximalDeployableConfiguration(String modelsDirectory) {
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();

    //get maximum number of elements as start point
    int maxSize = ((ASTSelect) findMaximalConfigurations(modelsDirectory).get(0)
      .getFCElement(0)).sizeNames();

    //load feature diagram from global scope and extract all configurations
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes()
      .get(0).getAstNode();
    List<ASTFeatureConfiguration> configurations = factTool.execAllProducts(featureDiagram);

    //now try deploying configurations
    while (maxSize > 0) {
      for (ASTFeatureConfiguration configuration : findConfigurationOfSize(configurations,
        maxSize)) {
        if (isConfigurationDeployable(modelsDirectory, configuration)) {
          outputList.add(configuration);
        }
      }
      if (outputList.size() != 0) {
        break;
      }
      maxSize--;
    }
    return outputList;
  }

  public boolean isConfigurationDeployable(String modelsDirectory, String configuration) {
    loadModel(modelsDirectory);
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(configuration,
      getModelPathFromModel(modelsDirectory));
    return isConfigurationDeployable(modelsDirectory, featureConfiguration);
  }

  public boolean isConfigurationDeployable(String modelsDirectory,
    ASTFeatureConfiguration configuration) {
    String result = tryDeployingConfig(modelsDirectory, configuration);
    return result.equals("{\"success\":true}");
  }

  //user chooses feature, generate rules and let prolog decide if we can deploy it
  public boolean isFeatureDeployable(String modelsDirectory, String feature) {
    //for now let factTool generate a featureconfig and use it
    loadModel(modelsDirectory);
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes()
      .get(0).getAstNode();
    ASTFeatureConfiguration configuration = factTool.execAllProducts(featureDiagram).get(0);

    //modify it so it only contains this feature and nothing else
    List<String> featuresList = new ArrayList<String>();
    featuresList.add(feature);
    configuration.setName("ConfigFor" + feature);
    ((ASTSelect) configuration.getFCElement(0)).setNameList(featuresList);
    ((ASTUnselect) configuration.getFCElement(1)).setNameList(new ArrayList<String>());

    return isConfigurationDeployable(modelsDirectory, configuration);
  }

  public List<ASTMCQualifiedName> findActivatedComponents(String modelsDirectory,
    String featureConfigurationDirectory) {
    ModelPath modelPath = getModelPathFromModel(featureConfigurationDirectory);
    loadModel(modelsDirectory);
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(
      featureConfigurationDirectory, modelPath);
    return findActivatedComponents(modelsDirectory, featureConfiguration);
  }

  public List<ASTMCQualifiedName> findActivatedComponents(String modelsDirectory,
    ASTFeatureConfiguration featureConfiguration) {
    ASTSelect selectedFeatures = (ASTSelect) featureConfiguration.getFCElement(0);
    ASTTagging tagging = loadModel(modelsDirectory);

    //Search through tags. If one tags refers to the feature name add all listed components.
    List<ASTMCQualifiedName> activatedComponents = new ArrayList<ASTMCQualifiedName>();
    for (String featureName : selectedFeatures.getNameList()) {
      for (ASTTag tag : tagging.getTagList()) {
        if (featureName.equals(tag.getFeature().getBaseName())) {
          for (ASTMCQualifiedName component : tag.getComponentsList()) {
            if (!activatedComponents.stream().filter(o -> o.getQName().equals(component.getQName()))
              .findFirst().isPresent()) {
              activatedComponents.add(component);
            }
          }
        }
      }
    }
    return activatedComponents;
  }

  public List<ASTMCQualifiedName> findPotentialFeatures(String modelsDirectory,
    List<ASTMCQualifiedName> activatedComponents) {
    //setup before execution
    ASTTagging tagging = loadModel(modelsDirectory);

    List<ASTMCQualifiedName> potentialFeatures = new ArrayList<ASTMCQualifiedName>();
    int numberOfActivatedComponents = 0;
    int numberOfActivatedFeatures = 0;
    //Check if all components of each tag are activated. If yes, consider feature as potential.
    for (ASTTag tag : tagging.getTagList()) {
      for (ASTMCQualifiedName component : tag.getComponentsList()) {
        if (activatedComponents.stream().filter(o -> o.getQName().equals(component.getQName()))
          .findFirst().isPresent()) {
          numberOfActivatedComponents++;
        }
      }
      if (numberOfActivatedComponents == tag.getComponentsList().size()) {
        potentialFeatures.add(tag.getFeature());
        numberOfActivatedFeatures++;
      }
      numberOfActivatedComponents = 0;
    }
    if (numberOfActivatedFeatures == tagging.getTagList().size())
      potentialFeatures.add(tagging.getPackage());
    return potentialFeatures;
  }

  public List<ASTMCQualifiedName> findPotentialExtraFeatures(String modelsDirectory,
    String featureConfigurationDirectory, List<ASTMCQualifiedName> activatedComponents) {
    List<ASTMCQualifiedName> allPotentialFeatures = findPotentialFeatures(modelsDirectory,
      activatedComponents);
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(
      featureConfigurationDirectory, getModelPathFromModel(modelsDirectory));
    ASTFeatures features = (ASTFeatures) featureConfiguration.getFCElement(0);
    List<String> activatedFeatures = features.getNameList();

    //Filter out features that are already in featureConfigurationDirectory.
    for (String feature : activatedFeatures) {
      allPotentialFeatures.removeIf(o -> o.getBaseName().equals(feature));
    }
    return allPotentialFeatures;
  }

  /*--------------------------------------------------------------------------------------------------------------------------
  --------------------------------------------------------------------------------------------------------------------------*/
  //simple return of activated instance
  public JsonObject activatedComponentsAsJSON(String modelsDirectory,
    ASTFeatureConfiguration configuration) {
    ASTTagging tagging = loadModel(modelsDirectory);

    JsonObject outputJson = new JsonObject();
    JsonArray componentsArray = new JsonArray();
    List<ASTMCQualifiedName> activatedComponents = findActivatedComponents(modelsDirectory,
      configuration);
    String qName = tagging.getPackage().getQName() + "." + tagging.getName() + ".";
    for (ASTMCQualifiedName component : activatedComponents) {
      componentsArray.add(qName + component.getQName());
    }
    outputJson.add("activatedComponents", componentsArray);
    return outputJson;
  }

  //given a configuration generate json for the prolog generator
  public JsonObject generateJSONFromConfiguration(String modelsDirectory, String configuration) {
    //setup models
    ASTTagging tagging = loadModel(modelsDirectory);
    ASTFeatureDiagram featureDiagram = tagging.getSymbol().getFeatureDiagramSymbol().get()
      .getAstNode();
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(configuration,
      getModelPathFromModel(modelsDirectory));

    //if (factTool.execIsValid(featureDiagram, featureConfiguration)) {
    return generateJSONFromConfiguration(modelsDirectory, featureConfiguration);
    //}
  }

  public JsonObject generateJSONFromConfiguration(String modelsDirectory,
    ASTFeatureConfiguration configuration) {
    try {
      //Load tagging model and extract qualified name to use later
      ASTTagging tagging = loadModel(modelsDirectory);

      //Parse deployment-info.json to a Json Object
      Path directory = Paths.get(modelsDirectory).getParent();
      String deploymentInfoLocation = directory.toString() + "/deployment-info.json";
      byte[] encoded = Files.readAllBytes(Paths.get(deploymentInfoLocation));
      String deploymentInfo = new String(encoded, StandardCharsets.UTF_8);
      JsonObject deploymentInfoJSON = JsonParser.parseString(deploymentInfo).getAsJsonObject();

      //Load feature diagram and check if configuration is even valid (disabled for now)
      ASTFeatureDiagram featureDiagram = tagging.getSymbol().getFeatureDiagramSymbol().get()
        .getAstNode();
      //if (factTool.execIsValid(featureDiagram, configuration)) {

      //Prepare the output JSON file and attach deployment info from earlier
      JsonObject outputJson = new JsonObject();
      JsonArray deploymentConstraintsArray = new JsonArray();
      outputJson.add("deploymentInfo", deploymentInfoJSON);

      //Access configurations of component instances list and then add them to json
      List<ASTMCQualifiedName> activatedComponents = findActivatedComponents(modelsDirectory,
        configuration);
      String qName = tagging.getPackage().getQName() + "." + tagging.getName() + ".";
      for (ASTMCQualifiedName component : activatedComponents) {
        JsonObject constraint = new JsonObject();
        constraint.addProperty("instanceSelector", qName + component.getQName());
        constraint.addProperty("type", "GREATER_EQUAL");
        constraint.addProperty("referenceValue", 1);
        constraint.addProperty("buildingSelector", "ANY");
        constraint.addProperty("floorSelector", "ANY");
        constraint.addProperty("roomSelector", "ANY");
        deploymentConstraintsArray.add(constraint);
      }
      outputJson.add("deploymentConstraint", deploymentConstraintsArray);

      return outputJson;
      //}
    }
    catch (IOException e) {
      Log.error("Deployment info file not found.");
      return null;
    }
  }

  public String communicateWithIoTManager(byte[] payload, String requestMethod, URL passedUrl) {
    try {
      URL url = passedUrl;
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(requestMethod);
      connection.setRequestProperty("Content-Length", String.valueOf(payload.length));
      connection.setDoOutput(true);
      connection.getOutputStream().write(payload);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] bufferArray = new byte[10 * 1024];
      int read;
      while ((read = connection.getInputStream().read(bufferArray)) >= 0) {
        outputStream.write(bufferArray, 0, read);
      }
      connection.getInputStream().close();

      return outputStream.toString();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String updateIoTManager(String modelsDirectory, String configuration) {
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(configuration,
      getModelPathFromModel(modelsDirectory));
    return updateIotManager(modelsDirectory, featureConfiguration);
  }

  public String updateIotManager(String modelsDirectory, ASTFeatureConfiguration configuration) {
    try {
      String generatedJSON = generateJSONFromConfiguration(modelsDirectory,
        configuration).toString();
      byte[] payload = generatedJSON.getBytes(StandardCharsets.UTF_8);

      return communicateWithIoTManager(payload, "PUT",
        new URL(iotManagerURL + "addFCToDeployment"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String tryDeployingConfig(String modelsDirectory, String configuration) {
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(configuration,
      getModelPathFromModel(modelsDirectory));
    return tryDeployingConfig(modelsDirectory, featureConfiguration);
  }

  public String tryDeployingConfig(String modelsDirectory, ASTFeatureConfiguration configuration) {
    try {
      String generatedJSON = generateJSONFromConfiguration(modelsDirectory,
        configuration).toString();
      byte[] payload = generatedJSON.getBytes(StandardCharsets.UTF_8);

      return communicateWithIoTManager(payload, "PUT", new URL(iotManagerURL + "tryDeployingFC"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String removeFeatureConstraint(String modelsDirectory,
    ASTFeatureConfiguration configuration) {
    try {
      String generatedJSON = generateJSONFromConfiguration(modelsDirectory,
        configuration).toString();
      byte[] payload = generatedJSON.getBytes(StandardCharsets.UTF_8);

      return communicateWithIoTManager(payload, "PUT",
        new URL(iotManagerURL + "removeFeatureConstraint"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

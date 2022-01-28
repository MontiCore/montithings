package tagging;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatures;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._parser.FeatureDiagramParser;
import de.monticore.featurediagram._symboltable.IFeatureDiagramGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import mcfdtool.FACT;
import mtconfig.MTConfigTool;
import org.antlr.v4.runtime.RecognitionException;
import de.se_rwth.commons.logging.Log;
import tagging._ast.ASTTag;
import tagging._symboltable.*;
import tagging._ast.ASTTagging;
import tagging._cocos.*;
import tagging._parser.TaggingParser;
import de.monticore.featurediagram.FeatureDiagramCLI;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.MontiThingsTool;
import tagging.cocos.*;

public class TaggingTool {

  protected ITaggingGlobalScope tgGS;
  protected IMontiThingsGlobalScope mtGS;
  protected IFeatureDiagramGlobalScope fdGS;
  protected MontiThingsTool mtTool = new MontiThingsTool();
  protected FeatureDiagramCLI fdCLI = new FeatureDiagramCLI();
  protected FACT factTool = new FACT();
  protected MTConfigTool mtConfigTool = new MTConfigTool();
  protected String iotManagerURL = "http://localhost:4210/";

  protected ModelPath getModelPathFromModel (String modelFile){
    Path directory = Paths.get(modelFile).getParent();
    return new ModelPath(directory);
  }

  public String getIotManagerURL() {
    return iotManagerURL;
  }

  public void setIotManagerURL(String url){
    this.iotManagerURL = url;
  }

  public static ASTTagging parse(String model) {
    try {
      TaggingParser parser = new TaggingParser() ;
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

  public ITaggingGlobalScope initTaggingGlobalScope (ModelPath modelPath){
    ITaggingGlobalScope globalScope = TaggingMill.globalScope();
    globalScope.clear();
    globalScope.setModelPath(modelPath);
    globalScope.setFileExt("tg");
    return globalScope;
  }

  public ASTTagging loadModel(String modelFile) {
      if (tgGS != null) {
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

  public void setUpScopes (String modelsDirectory){
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

  /*
  public boolean isConfigurationPossible(String modelsDirectory){
    //extract model path
    ModelPath modelPath = getModelPathFromModel(modelsDirectory);
    modelPath.addEntry(Paths.get("target"));

    //load tagging model
    ASTTagging tagging = loadModel(modelsDirectory);

    //load feature configuration (and diagram from global scope)
    //ASTFeatureDiagram featureDiagram = factTool.readFeatureDiagram(modelsDirectory + ".fd", "target", modelPath);
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes().get(0).getAstNode();
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(modelsDirectory + ".fc", modelPath);

    return isConfigurationPossible(tagging, featureDiagram, featureConfiguration);
  }

  public boolean isConfigurationPossible(ASTTagging tagging, ASTFeatureDiagram fd, ASTFeatureConfiguration fc){
    //Check if configuration is valid
    if(!factTool.execIsValid(fd, fc)){
      Log.warn(String.format("The Feature Configuration '%s' is not valid w.r.t the Feature Diagram.", fc.getName()));
      return false;
    }

    //Now check if the features are tagged to a component
    ASTSelect features = (ASTSelect) fc.getFCElement(0);
    boolean isTagged = false;
    int untaggedComponents = 0;
    for(String featureName : features.getNameList()){
      for (ASTTag tag : tagging.getTagList()){
        if (featureName.equals(tag.getFeature().getBaseName())){
          isTagged = true;
        }
      }
      if (!isTagged) {
        untaggedComponents++;
        Log.warn(String.format("The feature '%s' is not bound to a component", featureName));
      }
      isTagged = false;
    }
    return untaggedComponents == 0;
  }
*/
  public List<ASTFeatureConfiguration>  findMaximalConfigurations (String modelsDirectory){
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();

    //load feature diagram from global scope
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes().get(0).getAstNode();

    //if feature diagram is void don't bother continuing
    if (factTool.execIsVoidFeatureModel(featureDiagram)){
      Log.warn(String.format("The feature diagram '%s% does not have valid configurations", featureDiagram.getName()));
      return outputList;
    }

    //search for biggest number of elements
    List<ASTFeatureConfiguration> configurations = factTool.execAllProducts(featureDiagram);
    ASTSelect selectedFeatures;
    int maxSize = 0;
    for(ASTFeatureConfiguration candidate : configurations){
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() > maxSize){
          maxSize = selectedFeatures.getNameList().size();
        }
      }
    for(ASTFeatureConfiguration candidate : configurations){
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() == maxSize){
        outputList.add(candidate);
      }
    }
    return outputList;
  }

  public List<ASTFeatureConfiguration> findMinimalConfigurations (String modelsDirectory){
    List<ASTFeatureConfiguration> outputList = new ArrayList<ASTFeatureConfiguration>();

    //load feature diagram from global scope
    ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) fdGS.getSubScopes().get(0).getSubScopes().get(0).getAstNode();

    //if feature diagram is void don't bother continuing
    if (factTool.execIsVoidFeatureModel(featureDiagram)){
      Log.warn(String.format("The feature diagram '%s% does not have valid configurations", featureDiagram.getName()));
      return outputList;
    }

    //search for biggest number of elements
    List<ASTFeatureConfiguration> configurations = factTool.execAllProducts(featureDiagram);
    ASTSelect selectedFeatures;
    int minSize = Integer.MAX_VALUE;
    for(ASTFeatureConfiguration candidate : configurations){
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() < minSize){
        minSize = selectedFeatures.getNameList().size();
      }
    }
    for(ASTFeatureConfiguration candidate : configurations){
      selectedFeatures = (ASTSelect) candidate.getFCElement(0);
      if (selectedFeatures.getNameList().size() == minSize){
        outputList.add(candidate);
      }
    }
    return outputList;
  }

  public List<ASTFeatureConfiguration> findMinimalPotentialConfiguration (String modelsDirectory, List<ASTMCQualifiedName> activatedComponents){
    List<ASTFeatureConfiguration> minimalConfigurations = findMinimalConfigurations(modelsDirectory);
    List<ASTFeatureConfiguration> minimalPotentialConfigurations = new ArrayList<>();
    ASTSelect features;
    List<String> featureNames;
    List<ASTMCQualifiedName> activatedFeatures = findPotentialFeatures(modelsDirectory, activatedComponents);
    int foundFeatures = 0;

    for (ASTFeatureConfiguration configuration : minimalConfigurations){
      features = (ASTSelect) configuration.getFCElement(0);
      featureNames = features.getNameList();
      for (String name : featureNames){
        if (activatedFeatures.stream().filter(o -> o.getBaseName().equals(name)).findFirst().isPresent()) foundFeatures ++;
      }
      if (foundFeatures == featureNames.size()) minimalPotentialConfigurations.add(configuration);
      foundFeatures = 0;
    }
    return minimalPotentialConfigurations;
  }


  public List<ASTMCQualifiedName> findActivatedComponents (String modelsDirectory, String featureConfigurationDirectory){
    ModelPath modelPath = getModelPathFromModel(featureConfigurationDirectory);
    loadModel(modelsDirectory);
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(featureConfigurationDirectory, modelPath);
    return findActivatedComponents(modelsDirectory, featureConfiguration);
  }

  public List<ASTMCQualifiedName> findActivatedComponents (String modelsDirectory, ASTFeatureConfiguration featureConfiguration){
    ASTFeatures features = (ASTFeatures) featureConfiguration.getFCElement(0);
    ASTTagging tagging = loadModel(modelsDirectory);

    List<ASTMCQualifiedName> activatedComponents = new ArrayList<ASTMCQualifiedName>();
    for(String featureName : features.getNameList()){
      for(ASTTag tag : tagging.getTagList()){
        if (featureName.equals(tag.getFeature().getBaseName())){
          for (ASTMCQualifiedName component : tag.getComponentsList()){
            if (!activatedComponents.stream().filter(o -> o.getQName().equals(component.getQName())).findFirst().isPresent()){
              activatedComponents.add(component);
            }
          }
        }
      }
    }
    return activatedComponents;
  }

  public List<ASTMCQualifiedName> findPotentialFeatures (String modelsDirectory, List<ASTMCQualifiedName> activatedComponents){
    //setup before execution
    ASTTagging tagging = loadModel(modelsDirectory);

    List<ASTMCQualifiedName> potentialFeatures = new ArrayList<ASTMCQualifiedName>();
    int numberOfActivatedComponents = 0;
    int numberOfActivatedFeatures = 0;
    for (ASTTag tag : tagging.getTagList()){
      for (ASTMCQualifiedName component : tag.getComponentsList()){
        if (activatedComponents.stream().filter(o -> o.getQName().equals(component.getQName())).findFirst().isPresent()){
          numberOfActivatedComponents ++;
        }
      }
      if (numberOfActivatedComponents == tag.getComponentsList().size()){
        potentialFeatures.add(tag.getFeature());
        numberOfActivatedFeatures ++;
      }
      numberOfActivatedComponents = 0;
    }
    if (numberOfActivatedFeatures == tagging.getTagList().size()) potentialFeatures.add(tagging.getPackage());
    return potentialFeatures;
  }

  public List<ASTMCQualifiedName> findPotentialExtraFeatures (String modelsDirectory, String featureConfigurationDirectory, List<ASTMCQualifiedName> activatedComponents){
    List<ASTMCQualifiedName> allPotentialFeatures = findPotentialFeatures(modelsDirectory, activatedComponents);
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(featureConfigurationDirectory, getModelPathFromModel(modelsDirectory));
    ASTFeatures features = (ASTFeatures) featureConfiguration.getFCElement(0);
    List<String> activatedFeatures = features.getNameList();

    for (String feature : activatedFeatures){
        allPotentialFeatures.removeIf(o -> o.getBaseName().equals(feature));
    }
    return allPotentialFeatures;
  }
  //user chooses feature, generate rules and let prolog decide if we can deploy it
  public boolean isFeatureDeployable (String feature){
    return false;
  }


  //given a configuration generate json (choose existing json library and tell how the file would look) for the prolog generator
  public JsonObject generateJSONFromConfiguration (String modelsDirectory, String configuration){
    //setup models
    ASTTagging tagging = loadModel(modelsDirectory);
    ASTFeatureDiagram featureDiagram = tagging.getSymbol().getFeatureDiagramSymbol().get().getAstNode();
    ASTFeatureConfiguration featureConfiguration = factTool.readFeatureConfiguration(configuration, getModelPathFromModel(modelsDirectory));

    //if (factTool.execIsValid(featureDiagram, featureConfiguration)) {
    return generateJSONFromConfiguration(modelsDirectory, featureConfiguration);
    //}
  }

  public JsonObject generateJSONFromConfiguration (String modelsDirectory, ASTFeatureConfiguration configuration){
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
      ASTFeatureDiagram featureDiagram = tagging.getSymbol().getFeatureDiagramSymbol().get().getAstNode();
      //if (factTool.execIsValid(featureDiagram, configuration)) {

      //Prepare the output JSON file and attach deployment info from earlier
      JsonObject outputJson = new JsonObject();
      JsonArray deploymentConstraintsArray = new JsonArray();
      outputJson.add("deploymentInfo", deploymentInfoJSON);

      //Access configurations of component instances list and then add them to json
      List<ASTMCQualifiedName> activatedComponents = findActivatedComponents(modelsDirectory, configuration);
      String qName = tagging.getPackage().getQName() + "." + tagging.getName() + ".";
      for (ASTMCQualifiedName component : activatedComponents){
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
    } catch (IOException e) {
      Log.error("Deployment info file not found.");
      return null;
    }
  }

  public String updateIotManager (String modelsDirectory, String configuration) {
    try {
      String generatedJSON = generateJSONFromConfiguration(modelsDirectory, configuration).toString();
      byte[] payload = generatedJSON.getBytes(StandardCharsets.UTF_8);

      URL url = new URL(iotManagerURL + "addFCToDeployment");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("PUT");
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

      return outputStream.toString(StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  /*
  public void findMissingHardware(String modelsDirectory){
    //extract model path
    Path directory = Paths.get(modelsDirectory).getParent();
    //load tagging model
    ASTTagging tagging = loadModel(modelsDirectory);
    List<ASTMTConfigUnit> configsList = new ArrayList();

    //load configs
    mtConfigTool.setMtGlobalScope(mtGS);
    IMontiThingsGlobalScope mtcfgGS = mtConfigTool.initSymbolTable(directory.toFile());
    for (ASTMCQualifiedName component : tagging.getAllComponents()){
      configsList.add(mtConfigTool.processFile(directory.toString() + component.getBaseName() + "/.mtcfg"));
    }
  }*/

}

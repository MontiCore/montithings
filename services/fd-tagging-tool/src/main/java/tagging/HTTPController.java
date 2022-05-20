package tagging;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.monticore.featureconfiguration.FeatureConfigurationMill;
import de.monticore.featureconfiguration._ast.ASTFCElement;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfiguration._ast.ASTFeatureConfigurationBuilder;
import de.monticore.featureconfiguration._ast.ASTFeaturesBuilder;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTSelectBuilder;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._symboltable.FeatureDiagramSymbol;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.IOUtils;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HTTPController {
  //Entity that handles communication with tagging tool
  private final static String RESPONSE_JSON_SUCCESS = "{\"success\":true}";

  private final static String RESPONSE_JSON_FAILED = "{\"success\":false}";

  private final TaggingTool tool;

  public HTTPController(TaggingTool tool) {
    this.tool = tool;
  }

  public boolean start() {
    try {
      Spark.port(4220);
      Spark.put("/importFD", this::handleImportFD);
      Spark.put("/setFeatureConstraint", this::handleSetFeatureConstraint);
      Spark.put("/getMaximalConfigs", this::handleGetMaximalConfigs);
      Spark.put("/validateFeatureConfig", this::handleValidateFeatureConfig);
      Spark.put("/completeFeatureConfig", this::handleCompleteFeatureConfig);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  private Object handleImportFD(Request request, Response response) {
    try {
      //Convert input stream into a zip file
      String[] resplit = request.body().split(",");
      byte[] bytes = new byte[resplit.length];
      for (int i = 0; i < resplit.length; i++) {
        String numberString = resplit[i];
        bytes[i] = (byte) Integer.parseInt(numberString);
      }
      InputStream inputStream = new ByteArrayInputStream(bytes);

      File file = new File("/tmp/input.zip");
      Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

      //Unzip archive
      String outputDir = "/tmp/output";
      java.util.zip.ZipFile zipFile = new ZipFile(file);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        File entryDestination = new File(outputDir, entry.getName());
        if (entry.isDirectory()) {
          entryDestination.mkdirs();
        }
        else {
          entryDestination.getParentFile().mkdirs();
          try (InputStream in = zipFile.getInputStream(entry);
               OutputStream out = new FileOutputStream(entryDestination)) {
            IOUtils.copy(in, out);
          }
        }
      }

      //Find name of tagging file
      String taggingFile = "";
      Enumeration<? extends ZipEntry> entriesForTaggingSearch = zipFile.entries();
      while (entriesForTaggingSearch.hasMoreElements()) {
        ZipEntry entry = entriesForTaggingSearch.nextElement();
        String[] entryNameParts = entry.getName().split("\\.");
        String extension = "";
        if (entryNameParts.length > 0) {
          extension = entryNameParts[entryNameParts.length - 1];
        }
        if (extension.equals("tg")) {
          taggingFile = "/" + entryNameParts[0];
          break;
        }
      }
      //Load models
      String modelLocation = outputDir + taggingFile;
      tool.loadModel(modelLocation);
      List<String> features = new ArrayList<>();
      features.add(modelLocation);
      features.add(taggingFile);
      List<FeatureSymbol> featureSymbols = tool.fdGS.getSubScopes().get(0).getSubScopes().get(0)
        .getFeatureSymbols().values();
      for (FeatureSymbol featureSymbol : featureSymbols) {
        features.add(featureSymbol.getName());
      }
      return asJsonString(features);
    }
    catch (IOException e) {
      return RESPONSE_JSON_FAILED;
    }
  }

  private Object handleSetFeatureConstraint(Request request, Response response) {
    //Get request information
    JsonObject requestJSON = JsonParser.parseString(request.body()).getAsJsonObject();
    String taggingModelPath = requestJSON.get("modelPath").getAsString();
    String packageName = requestJSON.get("packageName").getAsString();
    String featureEntity = requestJSON.get("featureEntity").getAsString();

    //Load feature diagram and proceed only if it is loaded
    Optional<FeatureDiagramSymbol> fdSymbol = tool.fdGS.resolveFeatureDiagram(packageName);
    if (fdSymbol.isPresent()) {
      ASTFeatureDiagram featureDiagram = fdSymbol.get().getAstNode();
      ASTFeatureConfigurationBuilder builder = FeatureConfigurationMill.featureConfigurationBuilder();

      //Build feature configuration with only this feature and return the list of activated components
      builder.setName("ConfigFor " + featureEntity);
      builder.setFdName(featureDiagram.getName());

      List<ASTFCElement> fcelements = new ArrayList<>();
      List<String> featuresList = new ArrayList<>();
      featuresList.add(featureEntity);
      ASTSelectBuilder b = new ASTSelectBuilder();
      b.setNamesList(featuresList);
      fcelements.add(b.build());
      builder.setFCElementsList(fcelements);

      return tool.activatedComponentsAsJSON(taggingModelPath, builder.build()).toString();
    }
    else {
      return RESPONSE_JSON_FAILED;
    }
  }

  private Object handleGetMaximalConfigs(Request request, Response response) {
    //Load request
    JsonObject requestJSON = JsonParser.parseString(request.body()).getAsJsonObject();
    String taggingModelPath = requestJSON.get("modelPath").getAsString();
    String packageName = requestJSON.get("packageName").getAsString();
    int configSize = requestJSON.get("configSize").getAsInt();

    Optional<FeatureDiagramSymbol> fdSymbol = tool.fdGS.resolveFeatureDiagram(packageName);
    if (fdSymbol.isPresent()) {
      //Find all configs and build JSON response
      ASTFeatureDiagram featureDiagram = fdSymbol.get().getAstNode();
      List<ASTFeatureConfiguration> configs;
      if (configSize != 0) {
        configs = tool.findConfigurationOfSize(tool.factTool.execAllProducts(featureDiagram),
          configSize);
      }
      else {
        configs = tool.findMaximalConfigurations(taggingModelPath);
      }
      JsonObject outputJSON = new JsonObject();
      JsonArray configsJSON = new JsonArray();

      for (ASTFeatureConfiguration config : configs) {
        JsonArray featuresJSON = new JsonArray();
        ASTSelect features = (ASTSelect) config.getFCElement(0);
        for (String feature : features.getNameList()) {
          featuresJSON.add(feature);
        }
        configsJSON.add(featuresJSON);
      }
      outputJSON.add("configurations", configsJSON);
      return outputJSON;
    }
    else {
      return RESPONSE_JSON_FAILED;
    }
  }

  private Object handleValidateFeatureConfig(Request request, Response response) {
    JsonObject requestJson = JsonParser.parseString(request.body()).getAsJsonObject();
    String packageName = requestJson.get("packageName").getAsString();

    Optional<FeatureDiagramSymbol> fdSymbol = tool.fdGS.resolveFeatureDiagram(packageName);
    if (fdSymbol.isPresent()) {
      //Build feature configuration with supplied features and validate it against feature diagram
      ASTFeatureDiagram featureDiagram = fdSymbol.get().getAstNode();
      ASTFeatureConfigurationBuilder builder = FeatureConfigurationMill.featureConfigurationBuilder();

      JsonArray featuresArray = requestJson.getAsJsonArray("selectedFeatures");

      builder.setName("Config");
      builder.setFdName(featureDiagram.getName());

      List<ASTFCElement> fcelements = new ArrayList<>();
      List<String> featuresList = new ArrayList<>();
      for (JsonElement element : featuresArray) {
        featuresList.add(element.getAsString());
      }
      ;
      ASTFeaturesBuilder b = new ASTFeaturesBuilder();
      b.setNamesList(featuresList);
      fcelements.add(b.build());
      builder.setFCElementsList(fcelements);

      return tool.factTool.execIsValid(featureDiagram, builder.build()) ?
        RESPONSE_JSON_SUCCESS :
        RESPONSE_JSON_FAILED;
    }
    else {
      return "failed";
    }
  }

  private Object handleCompleteFeatureConfig(Request request, Response response) {
    JsonObject requestJson = JsonParser.parseString(request.body()).getAsJsonObject();
    String packageName = requestJson.get("packageName").getAsString();

    Optional<FeatureDiagramSymbol> fdSymbol = tool.fdGS.resolveFeatureDiagram(packageName);
    if (fdSymbol.isPresent()) {
      //Build feature configuration with selected features, execute FACT tool completion method and return JSON response.
      ASTFeatureDiagram featureDiagram = fdSymbol.get().getAstNode();
      ASTFeatureConfigurationBuilder builder = FeatureConfigurationMill.featureConfigurationBuilder();

      JsonArray featuresArray = requestJson.getAsJsonArray("selectedFeatures");

      builder.setName("Config");
      builder.setFdName(featureDiagram.getName());

      List<ASTFCElement> fcelements = new ArrayList<>();
      List<String> featuresList = new ArrayList<>();
      for (JsonElement element : featuresArray) {
        featuresList.add(element.getAsString());
      }
      ;
      ASTFeaturesBuilder b = new ASTFeaturesBuilder();
      b.setNamesList(featuresList);
      fcelements.add(b.build());
      builder.setFCElementsList(fcelements);

      boolean failQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      ASTFeatureConfiguration outputConfig = tool.factTool.execCompleteToValid(featureDiagram,
        builder.build());
      Log.getFindings().clear();
      Log.enableFailQuick(failQuick);
      if (outputConfig != null) {
        ASTSelect addedFeatures = (ASTSelect) outputConfig.getFCElement(0);
        JsonObject responseJson = new JsonObject();
        JsonArray outputFeatures = new JsonArray();
        for (String feature : addedFeatures.getNameList()) {
          outputFeatures.add(feature);
        }
        responseJson.add("newFeatures", outputFeatures);
        return responseJson.toString();
      }
      else {
        return RESPONSE_JSON_FAILED;
      }
    }
    else {
      return "failed";
    }
  }

  private String asJsonString(List<String> inputList) {
    //Helper function
    JsonObject output = new JsonObject();
    JsonArray featuresArray = new JsonArray();

    for (String feature : inputList) {
      featuresArray.add(feature);
    }
    output.add("features", featuresArray);
    return output.toString();
  }
}

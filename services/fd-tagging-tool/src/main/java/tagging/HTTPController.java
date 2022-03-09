package tagging;

import com.google.common.collect.LinkedListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.monticore.featureconfiguration._ast.ASTFeatureConfiguration;
import de.monticore.featureconfigurationpartial._ast.ASTSelect;
import de.monticore.featureconfigurationpartial._ast.ASTUnselect;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._symboltable.FeatureSymbol;
import spark.Request;
import spark.Response;
import spark.Spark;
import tagging._ast.ASTTagging;

import java.util.ArrayList;
import java.util.List;

public class HTTPController {

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
            Spark.put("/tryDeploy", this::handleTryDeploy);
            Spark.put("/updateManager", this::handleUpdateManager);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    private Object handleImportFD(Request request, Response response) {
        tool.loadModel(request.body());
        List<String> features = new ArrayList<>();
        List<FeatureSymbol> featureSymbols = tool.fdGS.getSubScopes().get(0).getSubScopes().get(0).getFeatureSymbols().values();
        for (FeatureSymbol featureSymbol : featureSymbols){
            features.add(featureSymbol.getName());
        }
        return asJsonString(features);
    }

    private Object handleTryDeploy(Request request, Response response){
        JsonObject requestJson = JsonParser.parseString(request.body()).getAsJsonObject();
        JsonArray featuresArray = (JsonArray) requestJson.get("selectedFeatures");
        ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) tool.fdGS.getSubScopes().get(0).getSubScopes().get(0).getAstNode();
        ASTFeatureConfiguration configuration = tool.factTool.execAllProducts(featureDiagram).get(0);

        List<String> featuresList = new ArrayList<>();
        for (JsonElement feature : featuresArray) {
            featuresList.add(feature.getAsString());
        }
        ((ASTSelect) configuration.getFCElement(0)).setNameList(featuresList);
        ((ASTUnselect) configuration.getFCElement(1)).setNameList(new ArrayList<>());

        String taggingModelPath = tool.tgGS.getModelPath().getFullPathOfEntries().stream().findAny().get().toString() + "/" + tool.tgGS.getSubScopes().get(0).getName();
        if (tool.isConfigurationDeployable(taggingModelPath, configuration)){
            return RESPONSE_JSON_SUCCESS;
        } else {
            return RESPONSE_JSON_FAILED;
        }
    }

    private Object handleUpdateManager(Request request, Response response){
        JsonObject requestJson = JsonParser.parseString(request.body()).getAsJsonObject();
        JsonArray featuresArray = (JsonArray) requestJson.get("selectedFeatures");
        ASTFeatureDiagram featureDiagram = (ASTFeatureDiagram) tool.fdGS.getSubScopes().get(0).getSubScopes().get(0).getAstNode();
        ASTFeatureConfiguration configuration = tool.factTool.execAllProducts(featureDiagram).get(0);

        List<String> featuresList = new ArrayList<>();
        for (JsonElement feature : featuresArray) {
            featuresList.add(feature.getAsString());
        }
        ((ASTSelect) configuration.getFCElement(0)).setNameList(featuresList);
        ((ASTUnselect) configuration.getFCElement(1)).setNameList(new ArrayList<>());

        String taggingModelPath = tool.tgGS.getModelPath().getFullPathOfEntries().stream().findAny().get().toString() + "/" + tool.tgGS.getSubScopes().get(0).getName();
        return tool.updateIotManager(taggingModelPath, configuration);
    }

    private String asJsonString(List<String> inputList) {
        JsonObject output = new JsonObject();
        JsonArray featuresArray = new JsonArray();

        for (String feature : inputList) {
            featuresArray.add(feature);
        }
        output.add("features", featuresArray);
        return output.toString();
    }
}

package montithings.services.openapi_generator.openapi.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.lang.json._ast.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OpenAPIToMTConverter {


  public String getComponentTypeName(ASTJSONValue json) {
    if (json instanceof ASTJSONObject) {
      List<ASTJSONProperty> webthingSchema = ((ASTJSONObject) json).getProps("webthing_schema");
      if (webthingSchema.size() == 1) {
        ASTJSONValue webthingSchemaValue = webthingSchema.get(0).getValue();
        if (webthingSchemaValue instanceof ASTJSONObject) {
          List<ASTJSONProperty> properties = ((ASTJSONObject) webthingSchemaValue).getProps("properties");
          if (properties.size() == 1) {
            ASTJSONValue propertyValue = properties.get(0).getValue();
            if (propertyValue instanceof ASTJSONObject) {
              List<ASTJSONProperty> componentType = ((ASTJSONObject) propertyValue).getProps("id");
              if (componentType.size() == 1) {
                ASTJSONValue componentTypeValue = componentType.get(0).getValue();
                if (componentTypeValue instanceof ASTJSONObject) {
                  List<ASTJSONProperty> componentTypeName = ((ASTJSONObject) componentTypeValue).getProps("default");
                  if (componentTypeName.size() == 1) {
                    ASTJSONValue name = componentTypeName.get(0).getValue();
                    if (name instanceof ASTJSONString) {
                      return ((ASTJSONString) name).getStringLiteral().getValue();
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return "";
  }

  public List<String> getIncomingPortNames(ASTJSONValue json) {
    if (json instanceof ASTJSONObject) {
      List<ASTJSONProperty> supportedProperties = ((ASTJSONObject) json).getProps("supported_properties");
      return getStringListFromJsonProperty(supportedProperties);
    }
    return Collections.emptyList();
  }

  public List<String> getStateVariables(ASTJSONValue json) {
    if (json instanceof ASTJSONObject) {
      List<ASTJSONProperty> supportedActions = ((ASTJSONObject) json).getProps("supported_actions");
      return getStringListFromJsonProperty(supportedActions);
    }
    return Collections.emptyList();
  }

  private List<String> getStringListFromJsonProperty(List<ASTJSONProperty> properties) {
    if (properties.size() == 1) {
      ASTJSONValue propertiesValue = properties.get(0).getValue();
      if (propertiesValue instanceof ASTJSONArray) {
        return ((ASTJSONArray) propertiesValue).getJSONValueList()
                .stream().filter(v -> v instanceof ASTJSONString)
                .map(v -> ((ASTJSONString) v).getStringLiteral().getValue())
                .collect(Collectors.toList());
      }
    }
    return Collections.emptyList();
  }

  /**
   * Generates a MontiThings model from an OpenAPI specification
   * @param node the AST of the OpenAPI specification in JSON-format
   */
  public static String generateMTModel(ASTJSONValue node) {
    GeneratorSetup setup = new GeneratorSetup();

    GeneratorEngine engine = new GeneratorEngine(setup);
    return engine.generate("templates/openapi.ftl", node).toString();
  }
}

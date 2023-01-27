// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Modelfinder;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.lang3.tuple.Pair;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Set;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class GenerateDeployInfo extends GeneratorStep {
    public static final String TF_EXTENSION = "tf";

    @Override
    public void action(GeneratorToolState state) {
        JsonObjectBuilder jsonBase = Json.createObjectBuilder();

        // Collect executable instances.
        JsonArrayBuilder jsonInstances = Json.createArrayBuilder();

        for (Pair<ComponentTypeSymbol, String> instance : state.getInstances()) {
            // Each executable instance will be added to the "instances" array.
            ComponentTypeSymbol comp = instance.getKey();
            JsonObjectBuilder jsonInstance = Json.createObjectBuilder();

            jsonInstance.add("componentType", comp.getFullName());
            jsonInstance.add("instanceName", instance.getValue());
            jsonInstance.add("dockerImage", comp.getFullName().toLowerCase() + ":latest");

            // Also add the requirements of the component.
            JsonArrayBuilder jreqs = Json.createArrayBuilder();
            for (String req : ComponentHelper.getRequirements(comp, state.getConfig())) {
                if (req.startsWith("ocl:")) {
                    jsonInstance.add("hardwareRequirements", req.substring(4));
                } else {
                    jreqs.add(req);
                }
            }
            jsonInstance.add("requirements", jreqs.build());

            JsonArrayBuilder terraformInfo = generateTerraformInfo(state, comp.getFullName());
            jsonInstance.add("terraformInfo", terraformInfo);

            jsonInstances.add(jsonInstance);
        }
        jsonBase.add("instances", jsonInstances.build());

        // Serialize JSON and write it to a file.
        String jsonString = jsonBase.build().toString();
        File jsonFile = new File(state.getTarget(), "deployment-info.json");
        FileReaderWriter.storeInFile(jsonFile.getAbsoluteFile().toPath(), jsonString);
    }

    private JsonArrayBuilder generateTerraformInfo(GeneratorToolState state, String componentType) {
        Set<File> foundTerraformFiles = Modelfinder.getModelFiles(TF_EXTENSION, state.getHwcPath());

        JsonArrayBuilder terraformInfos = Json.createArrayBuilder();

        for (File tf : foundTerraformFiles) {
            String fqnModelName = getDotSeperatedFQNModelName(state.getHwcPath().getPath(), tf.getPath(), TF_EXTENSION);

            // Tf file is matched to component, if tf filename starts with componentType
            // E.g. Example.mt <- matched -> Example.tf
            // E.g. Example.mt <- matched -> ExampleAnotherSuffix.tf
            // E.g. Example.mt <- not-matched -> PrefixExample.tf
            // Maintaining it as such allows multiple TF files per component
            if (fqnModelName.startsWith(componentType)) {
                Log.info("Generate TF Info: " + tf.toPath(), TOOL_NAME);

                try {
                    JsonObjectBuilder terraformInfo = Json.createObjectBuilder();
                    String encodedTf = fileToBase64Str(tf.toPath());
                    terraformInfo.add("filename", fqnModelName);
                    terraformInfo.add("filecontent", encodedTf);
                    terraformInfos.add(terraformInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return terraformInfos;
    }

    private String fileToBase64Str(Path path) throws IOException {
        byte[] fileContent = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    // Copy from MontiCore.Modelfinder
    private static String getDotSeperatedFQNModelName(String FQNModelPath, String FQNFilePath, String fileExtension) {
        if (FQNFilePath.contains(FQNModelPath)) {
            String fqnModelName = FQNFilePath.substring(FQNModelPath.length() + 1);
            fqnModelName = fqnModelName.replace("." + fileExtension, "");
            if (fqnModelName.contains("\\")) {
                fqnModelName = fqnModelName.replaceAll("\\\\", ".");
            } else if (fqnModelName.contains("/")) {
                fqnModelName = fqnModelName.replaceAll("/", ".");
            }

            return fqnModelName;
        }
        return FQNFilePath;
    }

}

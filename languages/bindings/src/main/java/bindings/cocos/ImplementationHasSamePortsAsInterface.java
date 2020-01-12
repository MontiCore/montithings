// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import de.monticore.ModelingLanguage;
import de.monticore.cd2pojo.Modelfinder;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import bindings._ast.ASTBindingRule;
import bindings._cocos.BindingsASTBindingRuleCoCo;
import bindings._symboltable.BindingsLanguage;
import montithings._symboltable.MontiThingsLanguage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checks that Implementation component has the same ports as Interface component
 */
public class ImplementationHasSamePortsAsInterface implements BindingsASTBindingRuleCoCo {
    private static final String APPLICATION_MODEL_PATH = "src/main/resources/models";
    private static final String TEST_MODEL_PATH = "src/test/resources/models";
    public static final String NOT_SAME_PORTS_IMPLEMENTED =
            "Implementation and Interface don't implement the same ports!";

    @Override
    public void check (ASTBindingRule node) {
        // Reads in implementation name
        String implementationName = node.getImplementationComponent().toString();
        String interfaceName = node.getInterfaceComponent().toString();

        // Extract lines from model
        ArrayList<String> extractedImplementation = readModel(implementationName);
        ArrayList<String> extractedInterface = readModel(interfaceName);

        // Parse single words out of model
        ArrayList<String[]> parsedImplementation = lineToWordArray(extractedImplementation);
        ArrayList<String[]> parsedInterface = lineToWordArray(extractedInterface);

        // Check the following:
        // 1. Implementation only contains ports that are defined in interface
        // 2. Interface only contains ports that are defined in implementation
        if (!checkPorts(parsedImplementation, parsedInterface)) {
            Log.error(NOT_SAME_PORTS_IMPLEMENTED);
        }
    }

    // Check if implementation and interface implement the same ports
    private boolean checkPorts(ArrayList<String[]> parsedImplementation, ArrayList<String[]> parsedInterface) {
        // 1. Implementation only contains ports that are defined in interface
        for (String[] line : parsedImplementation) {
            for (String word : line) {
                if (word.equals("in") || word.equals("out")) {
                    // interface must contain the same line
                    if (!containsSameWords(line, parsedInterface)) {
                        // interface doesn't contain the port declaration
                        return false;
                    }
                }
            }
        }
        // 2. Interface only contains ports that are defined in implementation
        for (String[] line : parsedInterface) {
            for (String word : line) {
                if (word.equals("in") || word.equals("out")) {
                    // implementation must contain the same line
                    if (!containsSameWords(line, parsedImplementation)) {
                        // implementation doesn't contain the port declaration
                        return false;
                    }
                }
            }
        }

        // return true if there was no failure before
        return true;
    }

    // Check if parsedModel contains wordsToContain
    private boolean containsSameWords(String[] wordsToContain, ArrayList<String[]> parsedModel) {
        for (String[] line : parsedModel) {
            if (Arrays.equals(wordsToContain, line)) {
                return true;
            }
        }
        return false;
    }

    // Removes keyword "port" from wordlist to allow both port definitions
    private ArrayList<String[]> removePortFromArray(ArrayList<String[]> lines) {
        // contains new line without keyword "port". Type ArrayList to add elements dynamically
        ArrayList<String> _newLine = new ArrayList<String>();
        // Index for lines position
        int index = 0;

        for(String[] line : lines) {
            // check if line contains keyword "port"
            if (line[0].equals("port")) {
                // remove keyword port
                for (int i=0; i<line.length; i++) {
                    if (i > 0) {
                        // Add each element except keyword "port"
                        _newLine.add(line[i]);
                    }
                }
                // Convert _newLine to regular Java array
                String[] newLine = _newLine.toArray(new String[_newLine.size()]);
                // Replace old line with keyword "port" with new line without keyword "port"
                lines.set(index, newLine);
            }
            // Clear _newLine
            _newLine.clear();
            // Increase index
            index++;
        }
        return lines;
    }

    // Extracts words from each line
    private ArrayList<String[]> lineToWordArray(ArrayList<String> lines) {
        ArrayList<String[]> wordsPerLine = new ArrayList<String[]>();
        ArrayList<String> removeEmptyStrings = new ArrayList<String>();
        ArrayList<Integer> emptyStringIndices = new ArrayList<Integer>();

        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (int i=0; i<words.length; i++) {
                // parse to single words
                words[i] = words[i].replaceAll("[^\\w]", "");
            }
            // Find empty strings
            for (int i=0; i<words.length; i++) {
                if (words[i].equals("")) {
                    emptyStringIndices.add(i);
                }
            }
            // remove empty strings from array
            removeEmptyStrings = new ArrayList<String>(Arrays.asList(words));
            for (Integer i : emptyStringIndices) {
                removeEmptyStrings.remove((int)i);
            }

            // skip empty lines
            if (removeEmptyStrings.size() == 0) {
                emptyStringIndices.clear();
                continue;
            }

            // Parse back to java array
            String[] wordsWithoutEmptyString = removeEmptyStrings.toArray(new String[removeEmptyStrings.size()]);

            // Add parsed line to wordsPerLine
            wordsPerLine.add(wordsWithoutEmptyString);

            // clear array
            emptyStringIndices.clear();
        }

        // Return wordsPerLine with keyword "port" removed from each line
        return removePortFromArray(wordsPerLine);
    }

    // Reads in given modelName line by line
    private ArrayList<String> readModel(String modelName) {
        // Models are either under "src/test/resources/models" or "src/main/resources/models"
        // modelSubDirs contains all model files of the test or application
        List<String> foundModels;
        boolean application = true;
        try {
            foundModels = Modelfinder.getModelsInModelPath(Paths.get(APPLICATION_MODEL_PATH).toFile(),
                    MontiThingsLanguage.FILE_ENDING);
        } catch (Exception e) {
            foundModels = Modelfinder.getModelsInModelPath(Paths.get(TEST_MODEL_PATH).toFile(),
                    MontiThingsLanguage.FILE_ENDING);
            application = false;
        }

        // Every entry contains 1 line of the model
        ArrayList<String> modelLines = new ArrayList<String>();

        // Read implementation and interface model line by line and check if they implement the same ports
        // Pass Coco if they do
        for (String model : foundModels) {
            String qualifiedModelName = Names.getSimpleName(model);
            if (qualifiedModelName.equals(modelName)) {
                // Append all lines to modelLines
                try {
                    String modelPath;
                    if (application) {
                        modelPath = new File(APPLICATION_MODEL_PATH).getAbsolutePath();
                    }
                    else {
                        modelPath = new File(TEST_MODEL_PATH + "/" + model + ".mt").getAbsolutePath();
                    }
                    BufferedReader reader = new BufferedReader(new FileReader(modelPath));
                    String line = reader.readLine();
                    while (line != null) {
                        modelLines.add(line);
                        line = reader.readLine();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // return read in model
        return modelLines;
    }
}
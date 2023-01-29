// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.MessageBroker;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.GeneratorHelper;
import montithings.generator.steps.GeneratorStep;
import montithings.generator.steps.helper.GenerateCDEAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.io.FileUtils;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;
import static montithings.generator.helper.ComponentHelper.getDynamicallyConnectedSubcomps;
import static montithings.generator.helper.FileHelper.copyHwcToTarget;
import static montithings.generator.helper.FileHelper.getSubPackagesPath;

public class GenerateHTML extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {

    ArrayList<String> languagePaths = getAllLanguageDirectories(state.getConfig());
    ArrayList<String> instanceNames = new ArrayList<>();
    for(Pair<ComponentTypeSymbol,String> pair : state.getInstances()){
      if(ComponentHelper.isDSLComponent(pair.getKey(), state.getConfig())){
        
        String explain = "No explaination for this language available.";
        try{
          File explaination = new File(state.getConfig().getLanguagePath().getPath() + "/" + pair.getKey().getFullName().replace(".","/") + "/" + "EXPLAIN.txt");
          explain = FileUtils.readFileToString(explaination,"UTF-8");
          explain = explain.replace("\"","&quot;");
        }
        catch(IOException e){
          System.out.println("GenerateHTML couldn't find explaination file. Following IOException has been thrown:" + e.getMessage());
        }



        instanceNames.add(pair.getValue().replace(".","/"));
        state.getMtg().generateHTMLFilesForDSLs(new File (state.getTarget().getAbsolutePath()),state.getConfig(), pair.getValue(), explain);
      }
    }
    String explainProj = "No project description available!";
    try{
      File explainationProj = new File(state.getConfig().getLanguagePath().getPath() + "/" + "EXPLAIN.txt");
      explainProj = FileUtils.readFileToString(explainationProj,"UTF-8");
      explainProj = explain.replace("\"","&quot;");
    }
    catch(IOException e){
      System.out.println("GenerateHTML couldn't find explaination file for the project. Following IOException has been thrown:" + e.getMessage());
    }
    state.getMtg().generateHTMLIndexFile(new File (state.getTarget().getAbsolutePath()),state.getConfig(),instanceNames, explainProj);

  }
  
   /**
   * Get the full path of all Languages in the Langauge folder
   */
  public static ArrayList<String> getAllLanguageDirectories(ConfigParams config) {
    File languagesFolder = config.getLanguagePath();
    if(languagesFolder.isDirectory()){
      ArrayList<String> languageDirectories = recursiveSearchLanguageFiles(languagesFolder,"");
      return languageDirectories;
    }
    return new ArrayList<String>();
  }

  static ArrayList<String> recursiveSearchLanguageFiles(File rootFile, String path){
    ArrayList<String> retVal = new ArrayList<String>();
    File[] subDirs = rootFile.listFiles();
    for(File file : subDirs){
      if(file.isDirectory() && file.getName().equals("src")){
        retVal.add(path);
        return retVal;
      }
    }
    for(File file : subDirs){
      if(file.isDirectory()){
        retVal.addAll(recursiveSearchLanguageFiles(file,path + "/" + file.getName()));
      }
    }
    return retVal;
  }

}

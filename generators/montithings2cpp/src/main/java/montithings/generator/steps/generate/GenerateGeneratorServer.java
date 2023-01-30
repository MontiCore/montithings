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
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;
import static montithings.generator.helper.ComponentHelper.getDynamicallyConnectedSubcomps;
import static montithings.generator.helper.FileHelper.copyHwcToTarget;
import static montithings.generator.helper.FileHelper.getSubPackagesPath;

public class GenerateGeneratorServer extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if(state.getConfig().getLanguagePath() != null){
      ArrayList<String> languagePaths = getAllLanguageDirectories(state.getConfig());
      state.getMtg().generateGeneratorServer(new File (state.getTarget().getAbsolutePath()),state.getConfig(),languagePaths,state);
    }
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

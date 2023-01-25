<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::python_receiver(std::string payload){
    std::fstream modelFile;
    modelFile.open("python/${className}Impl.py",std::fstream::out);
    modelFile << payload;
    modelFile.close();

    int pid = fork();
    if(pid == 0){
      if(lastPyPID != -1){
        kill(lastPyPID,SIGKILL);
      }
      char *intrepreter="python3"; 
      char *pythonPath="python/${className}.py"; 
      char *pythonArgs[]={intrepreter,pythonPath,NULL};
      execvp(intrepreter,pythonArgs);
    }
    else{
      signal(SIGCHLD,SIG_IGN);
      std::this_thread::sleep_for(std::chrono::milliseconds(2000));
      mqttClientInstance->publish (replaceDotsBySlashes ("/components"), replaceDotsBySlashes (instanceName));
      lastPyPID = pid;
    } 
  }
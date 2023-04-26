<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

// This method is triggered when a new python script is received, it it stores the file and triggers the function taht replaces the currently running process. (Part of the EUD feature)
${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::python_receiver(std::string payload){
    std::fstream modelFile;
    modelFile.open("python/${className}Impl.py",std::fstream::out);
    modelFile << payload;
    modelFile.close();

    python_start();
  }

//This method executes new python scripts and termiantes old scripts. (Part of the EUD feature)
${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::python_start(){
  int pid = fork();
  if(pid == 0){
    if(lastPyPID != -1){
      kill(lastPyPID,SIGKILL);
    }
    std::string interpreter="python3";
    std::string pythonPath="python/${className}.py";
    std::string hostArg = "--host=" + brokerHostName;
    std::string portArg = "--port=" + std::to_string(brokerPort);
    std::string nameArg = "--name=" + instanceName;
    char *pythonArgs[]={strdup(interpreter.data()),strdup(pythonPath.data()),strdup(hostArg.data()),strdup(portArg.data()),strdup(nameArg.data()),NULL};
    execvp(strdup(interpreter.data()),pythonArgs);
  }
  else{
    signal(SIGCHLD,SIG_IGN);
    std::this_thread::sleep_for(std::chrono::milliseconds(2000));
    mqttClientInstance->publish (replaceDotsBySlashes ("/components"), replaceDotsBySlashes (instanceName));
    lastPyPID = pid;
  } 
}
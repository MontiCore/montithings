<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::python_receiver(){
    int lastpid = -1;

    httplib::Server svr;
    svr.Post("/Py", [&](const httplib::Request &req, httplib::Response &res) {
      const auto& file = req.get_param_value("fileUpload");
      std::fstream modelFile;
      modelFile.open("python/${className}Impl.py",std::fstream::out);
      modelFile << file;
      modelFile.close();
      int pid = fork();
      if(pid == 0){
        if(lastpid != -1){
          kill(lastpid,SIGKILL);
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
        lastpid = pid;
      }
    });
    

    svr.listen("0.0.0.0", 8081);    
  }
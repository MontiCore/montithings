<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">


${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::python_receiver(){
    uint16_t port = 8081;
    int server_fd, new_socket, pid; 
    long valread;
    struct sockaddr_in address;
    int addrlen = sizeof(address);

    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
    {
    perror("In sockets");
    exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons( port );
    memset(address.sin_zero, '\0', sizeof address.sin_zero);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address))<0)
    {
        perror("In bind");
        close(server_fd);
        exit(EXIT_FAILURE);
    }
    if (listen(server_fd, 10) < 0)
    {
        perror("In listen");
        exit(EXIT_FAILURE);
    }
    int lastpid = -1;
    while(1)
    {
      if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen))<0)
      {
          perror("In accept");
          exit(EXIT_FAILURE);
      }

      char buffer[30000] = {0};
      valread = read( new_socket , buffer, 30000);

      printf("\n buffer message: %s \n ", buffer);
      char *buffer_copy = (char *)malloc(strlen(buffer) + 1);
      strcpy(buffer_copy,buffer);

      if(buffer_copy[0] == 'P' && buffer_copy[1] == 'Y' && buffer_copy[2] == ' ')
      {
        pid = fork();
        if(pid < 0){
          perror("Error on fork");
          exit(EXIT_FAILURE);
        }
        
        if(pid == 0)
        {
        
          buffer_copy = buffer_copy + 3;
          LOG(DEBUG) << "PY message: " << buffer_copy;
          std::fstream pyFile;
          //pyFile.open("python/SinkImpl.py",std::ios_base::out);
          //pyFile << buffer_copy;
          //pyFile.close();
          free(buffer_copy - 3);

          if(lastpid != -1){
            kill(lastpid,SIGKILL);
          }
          char *intrepreter="python3"; 
          char *pythonPath="python/${className}.py"; 
          char *pythonArgs[]={intrepreter,pythonPath,NULL};
          execvp(intrepreter,pythonArgs);
        }else
        {
          signal(SIGCHLD,SIG_IGN);
          lastpid = pid;
          std::this_thread::sleep_for(std::chrono::milliseconds(2000));
          mqttClientInstance->publish (replaceDotsBySlashes ("/components"), replaceDotsBySlashes (instanceName));
        }
      }else{
        LOG(DEBUG) << "Not a Py File: " << buffer_copy;
        free(buffer_copy);
      }

      close(new_socket);
    }
    close(server_fd);
        
  }
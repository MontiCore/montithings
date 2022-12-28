<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::website_hoster(){


  uint16_t port = 8080;
  int server_fd, new_socket, pid;
  long valread;
  struct sockaddr_in address;
  int addrlen = sizeof(address);
  
  // Creating socket file descriptor
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
  
  while(1)
  {
    if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen))<0)
    {
        perror("In accept");
        exit(EXIT_FAILURE);
    }
    pid = fork();
    if(pid < 0){
        perror("Error on fork");
        exit(EXIT_FAILURE);
    }
    
    if(pid == 0){
      char buffer[30000] = {0};
      valread = read( new_socket , buffer, 30000);

      LOG(DEBUG) << "\n buffer message: " << buffer;

      std::string parse_string_method = get_web_substring(buffer,0); 

      std::cout << "Client method: " << parse_string_method << std::endl;

      char httpHeader1[800021] = "HTTP/1.1 200 OK\r\n\n";

      std::string parse_string = get_web_substring(buffer,1);  
      std::cout << "Client path: " << parse_string << std::endl;

      char *copy_head = (char *)malloc(strlen(httpHeader1) +200);
      strcpy(copy_head, httpHeader1);

      if(parse_string_method[0] == 'G' && parse_string_method[1] == 'E' && parse_string_method[2] == 'T'){
        //https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
        if(parse_string.length() <= 1){
          char path_head[500] = ".";
          strcat(path_head, "/index2.html");
          strcat(copy_head, "Content-Type: text/html\r\n\r\n");
          send_message(new_socket, path_head, copy_head);


          int sockfd = 0;
          int client_fd;
          struct sockaddr_in serv_addr;
          char* hello = "PY Hello from client";
          char buffer[1024] = { 0 };
          if((sockfd = socket(AF_INET,SOCK_STREAM,0)) < 0)
          {
            perror("In Client sockets");
            exit(EXIT_FAILURE);
          }

          serv_addr.sin_family = AF_INET;
          serv_addr.sin_port = htons(8081);
          if(inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr) <= 0)
          {
            perror("In Address convertion");
            exit(EXIT_FAILURE);
          }
          if ((client_fd = connect(sockfd, (struct sockaddr*) &serv_addr, sizeof(serv_addr)))< 0) {
            perror("In Connection");
            exit(EXIT_FAILURE);
          }
          send(sockfd, hello, strlen(hello), 0);
          LOG(DEBUG) << "\n -send";
          close(client_fd);
        }
      }

      close(new_socket);
      free(copy_head);
      exit(1);
    }
    else{
      signal(SIGCHLD,SIG_IGN);
      LOG(DEBUG) << ">>>>>>>>>>Parent create child with pid: " << pid << "<<<<<<<<<";
      close(new_socket);
    }
  
  }
  close(server_fd);
}
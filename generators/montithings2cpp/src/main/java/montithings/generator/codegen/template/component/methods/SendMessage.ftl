<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::send_message(int fd, char path[], char head[]){
    struct stat stat_buf;  /* hold information about input file */

    write(fd, head, strlen(head));

    int fd_website = open(path, O_RDONLY);
    
    if(fd_website < 0){
        printf("Cannot Open file path : %s with error %d\n", path, fd_website); 
    }
     
    fstat(fd_website, &stat_buf);
    int total_size = stat_buf.st_size;
    int block_size = stat_buf.st_blksize;

    if(fd_website >= 0){
        ssize_t sent_size;

        while(total_size > 0){
            
              int send_bytes = ((total_size < block_size) ? total_size : block_size);
              int done_bytes = sendfile(fd, fd_website, NULL, send_bytes);
              total_size = total_size - done_bytes;
        }
        if(sent_size >= 0){
            printf("send file: %s \n" , path);
        }
        close(fd_website);
    }
}
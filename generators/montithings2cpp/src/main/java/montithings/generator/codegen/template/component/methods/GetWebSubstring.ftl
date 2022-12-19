<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
std::string ${className}${Utils.printFormalTypeParameters(comp)}::get_web_substring(char* buffer, int segment){

    std::string input(buffer);
    std::string output;
    int i = 0;
    int pos = 0;
    int n = 0;
    while(n <= segment){
      
      i = 0;
      while(input.substr(pos + i,1) != " "){
        i++;
      }
      n++;
      output = input.substr(pos,i);
      pos = pos + i + 1;
    }
    return output;
} 
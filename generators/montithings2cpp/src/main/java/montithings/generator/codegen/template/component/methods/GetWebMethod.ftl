<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
char* ${className}${Utils.printFormalTypeParameters(comp)}::get_web_method(char* buffer){

    char* method = "";
    int i = 0;
    while (buffer[i] != " "){
        char* += buffer[i];
        i++;
    }
    return method;
}
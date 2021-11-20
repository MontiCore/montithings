<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type")}

InOutPort<${type}>* ${port}Interface::getPort${name?cap_first}(){
return ${name};
}
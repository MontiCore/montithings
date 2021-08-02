<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type", "existsHWC")}

double ${port}Interface::getPort${name?cap_first}ConversionFactor(){
return ${name}ConversionFactor;
}
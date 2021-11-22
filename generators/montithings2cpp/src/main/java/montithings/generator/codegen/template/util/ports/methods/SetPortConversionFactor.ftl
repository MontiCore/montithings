<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type")}

void ${port}Interface::setPort${name?cap_first}ConversionFactor(double factor){
${name}ConversionFactor = factor;
}
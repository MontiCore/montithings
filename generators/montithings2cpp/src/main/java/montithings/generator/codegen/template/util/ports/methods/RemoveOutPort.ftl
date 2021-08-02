<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type", "existsHWC")}

void ${port}Interface::removeOutPort${name?cap_first}(Port<${type}>* port){
${name}->getOutport ()->removeManagedPort (port);
}
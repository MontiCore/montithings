<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type", "existsHWC")}

void ${port}Interface::removeInPort${name?cap_first}(Port<${type}>* port){
${name}->getInport ()->removeManagedPort (port);
}
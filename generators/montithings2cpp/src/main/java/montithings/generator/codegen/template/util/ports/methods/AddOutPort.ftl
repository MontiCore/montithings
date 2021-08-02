<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type", "existsHWC")}

void ${port}Interface::addOutPort${name?cap_first}(Port<${type}>* port){
${name}->getOutport ()->addManagedPort (port);
}
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "config", "name", "type")}

void ${port}Interface::addInPort${name?cap_first}(Port<${type}>* port){
${name}->getInport ()->addManagedPort (port);
}
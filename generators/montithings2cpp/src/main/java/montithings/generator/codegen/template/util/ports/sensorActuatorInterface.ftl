<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "type", "config", "existsHWC")}

#pragma once
#include "InOutPort.h"
#include "easyloggingpp/easylogging++.h"

class ${port}Interface
{
protected:
InOutPort<${type}>* in = new InOutPort<${type}>();
double inConversionFactor = 1;
InOutPort<${type}>* out = new InOutPort<${type}>();
double outConversionFactor = 1;

public:
InOutPort<${type}>* getPortIn();
void addInPortIn(Port<${type}>* in);
void removeInPortIn(Port<${type}>* in);
void setPortInConversionFactor(double inConversionFactor);
double getPortInConversionFactor();

InOutPort<${type}>* getPortOut();
void addOutPortOut(Port<${type}>* out);
void removeOutPortOut(Port<${type}>* out);
void setPortOutConversionFactor(double outConversionFactor);
double getPortOutConversionFactor();

};


${tc.includeArgs("template.util.ports.methods.GetPort", [port, config, "in", type, existsHWC])}
${tc.includeArgs("template.util.ports.methods.GetPort", [port, config, "out", type, existsHWC])}

${tc.includeArgs("template.util.ports.methods.AddInPort", [port, config, "in", type, existsHWC])}
${tc.includeArgs("template.util.ports.methods.RemoveInPort", [port, config, "in", type, existsHWC])}

${tc.includeArgs("template.util.ports.methods.AddOutPort", [port, config, "out", type, existsHWC])}
${tc.includeArgs("template.util.ports.methods.RemoveOutPort", [port, config, "out", type, existsHWC])}

${tc.includeArgs("template.util.ports.methods.GetPortConversionFactor", [port, config, "in", type, existsHWC])}
${tc.includeArgs("template.util.ports.methods.GetPortConversionFactor", [port, config, "out", type, existsHWC])}

${tc.includeArgs("template.util.ports.methods.SetPortConversionFactor", [port, config, "in", type, existsHWC])}
${tc.includeArgs("template.util.ports.methods.SetPortConversionFactor", [port, config, "out", type, existsHWC])}


<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName","comp","config")}
<#include "/template/Preamble.ftl">

from ${componentName}Impl import ${componentName}Impl
<#if ComponentHelper.isDSLComponent(comp,config)>
import argparse, sys
</#if>

if __name__=="__main__":
    connector = ${componentName}Impl()
<#if ComponentHelper.isDSLComponent(comp,config)>
    parser=argparse.ArgumentParser()
    parser.add_argument("--host", help="mqtt-broker ip")
    parser.add_argument("--port", help="mqtt-broker port")
    args = vars(parser.parse_args())
    if "port" in args:
        args["port"] = int(1883 if args.get("port") is None else args.get("port"))

    connector = ${componentName}Impl()
    # Block forever
    connector.connect(**args)
<#else>
    # Block forever
    connector.connect()
</#if>
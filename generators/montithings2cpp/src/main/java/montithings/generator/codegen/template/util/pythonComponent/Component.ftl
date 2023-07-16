<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName","comp","config")}
<#include "/template/Preamble.ftl">

from ${componentName}Impl import ${componentName}Impl
import argparse, sys

if __name__=="__main__":
<#if ComponentHelper.isDSLComponent(comp,config)>
    parser=argparse.ArgumentParser()
    parser.add_argument("--host", help="mqtt-broker ip")
    parser.add_argument("--port", help="mqtt-broker port")
    parser.add_argument("--name", help="mqtt-broker id")
    args = vars(parser.parse_args())
    if "port" in args:
        args["port"] = int(1883 if args.get("port") is None else args.get("port"))

    connector = ${componentName}Impl(args["name"])
    # Block forever
    connector.connect(host=args["host"], port=args["port"])
<#else>
    parser = argparse.ArgumentParser(description='Coordinate sensor / actuator access')
    parser.add_argument('--brokerPort', nargs='?', default=1883, type=int,
                        help='Network port of the MQTT broker')
    parser.add_argument('--brokerHostname', nargs='?', default='localhost', type=str,
                        help='Hostname of the MQTT broker')
    parser.add_argument('--name', nargs='?', default=__name__, type=str,
                        help='Identifier for this python component (e.g. MQTT-Communication)')
    args_temp, unknown = parser.parse_known_args()

    connector = ${componentName}Impl(args_temp.name)
    # Block forever
    connector.connect()
</#if>
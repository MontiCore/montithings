<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("port", "comp", "config", "existsHWC")}
<#include "/template/result/helper/GeneralPreamble.ftl">

<#assign name = port.getName()>
<#assign type = ComponentHelper.getRealPortCppTypeString(comp, port, config)>

${Utils.printTemplateArguments(comp)}
Message<${type}>
${className}${Utils.printFormalTypeParameters(comp, false)}::get${name?cap_first}Message(sole::uuid id) const
{
    Message<${type}> message = Message<${type}>();
    message.setUuid(id);

    if (${name}.has_value()) {
        message.setPayload(${name}.value());
    }

    return message;
}

${tc.signature("type", "typeName", "super", "associations")}

using ProtocolBuffer = ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName};

/// Constructor for deserialization from Protocol Buffer messages
explicit ${typeName}(const ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName}& other)
<#if super != "" || type.fieldList?size != 0>
    : <#-- Initializer for the parent class -->
    <#if super != "">${super}{other.super()}
        <#if type.fieldList?size != 0>,</#if>
    </#if>
<#-- Initializers for members -->
    <#list type.fieldList as field>
        ${field.name}{other.${field.name}()}<#sep>,
    </#list>
</#if>
{
<#list associations as assoc>
<#-- TODO: associations probably need some special handling for e.g. optionals and repeated -->
    <#assign assocName=AssociationHelper.getDerivedName(assoc, type)>
    this->${assocName} = other.${assocName}();
</#list>
}

/// Member method for serialization to Protocol Buffer messages
auto make_protobuffer() const -> ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName} {
<#-- Take an fresh and empty message -->
auto msg = ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName}{};

<#-- Handle possible parent base class -->
<#if super != "">
    { // Copy the parent class into the message
    auto* super = msg.mutable_super();
    *super = static_cast<${super}*>(this)->make_protobuffer();
    }
</#if>

<#-- Handle fields -->
<#list type.fieldList>
    // Set all fields
    <#items as field>
        msg.set_${field.name}(this->${field.name});
    </#items>
</#list>

<#-- Handle associations -->
<#list associations>
    // Set all associations
    <#items as assoc>
        {
        <#assign assocName=AssociationHelper.getDerivedName(assoc, type)>
        auto * ${assocName}_p = msg.mutable_${assocName}();
        *${assocName}_p = this->${assocName}.make_protobuffer();
        }
    </#items>
</#list>

return msg;
}

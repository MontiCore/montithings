${tc.signature("AssociationHelper", "type", "typeName", "super", "associations")}

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
    <#assign assoc_t=AssociationHelper.getOtherSideTypeName(assoc, type)>
    {
    // copy ${assocName}
    <#if AssociationHelper.getOtherSideCardinality(assoc, type).isMult() >
        this->${assocName}.reserve (other.${assocName} ().size ());
        std::transform (other.${assocName} ().cbegin (), other.${assocName} ().cend (),
          this->${assocName}.begin (),
          [] (const ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${assoc_t} &item) { return ${assoc_t}{ item }; });
    <#elseif AssociationHelper.getOtherSideCardinality(assoc, type).isOpt() >
        if (other.${assocName} ().empty ()) {
            this->${assocName} = tl::nullopt;
        } else {
            this->${assocName} = ${assoc_t}{ *other.${assocName} ().begin () };
        }
    <#else >
        this->${assocName} = ${assoc_t}{other.${assocName}()};
    </#if>
    }
</#list>
}

/// Member method for serialization to Protocol Buffer messages
auto to_protobuffer() const -> ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName} {
<#-- Take an fresh and empty message -->
auto msg = ${type.getEnclosingScope().getEnclosingScope().getName()}::protobuf::${typeName}{};

<#-- Handle possible parent base class -->
<#if super != "">
    { // Copy the parent class into the message
    auto* super = msg.mutable_super();
    *super = make_protobuffer(static_cast<${super}>(*this));
    }
</#if>

<#-- Handle fields -->
<#list type.fieldList>
    // Set all fields
    {
    <#items as field>
        msg.set_${field.name}(this->${field.name});
    </#items>
    }
</#list>
<#-- Handle associations -->
<#list associations>
    // Set all associations
    <#items as assoc>
        <#assign assocName=AssociationHelper.getDerivedName(assoc, type)>
        <#assign assoc_t=AssociationHelper.getOtherSideTypeName(assoc, type)>
        {
        // copy ${assocName}
        <#if AssociationHelper.getOtherSideCardinality(assoc, type).isMult() >
            msg.mutable_${assocName}()->Reserve (this->${assocName}.size());
            std::transform (this->${assocName}.cbegin(), this->${assocName}.cend(),
              msg.mutable_${assocName}()->begin(),
              [](const ${assoc_t}& item){ return make_protobuffer (item); });
        <#elseif AssociationHelper.getOtherSideCardinality(assoc, type).isOpt() >
            if (${assocName}.has_value()) {
                auto *${assocName}_p = msg.mutable_${assocName} ();
                ${assocName}_p->Clear();
                *${assocName}_p->begin() = make_protobuffer (this->${assocName}.value());
            } else {
                msg.mutable_${assocName}()->Clear();
            }
        <#else >
            auto * ${assocName}_p = msg.mutable_${assocName}();
            *${assocName}_p = make_protobuffer(this->${assocName});
        </#if>
        }
    </#items>
</#list>

return msg;
}

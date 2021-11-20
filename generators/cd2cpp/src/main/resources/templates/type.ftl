<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("namespaceCount", "package", "kind", "type", "super", "typeHelper", "imports", "associations", "existsHwc")}
<#assign AssociationHelper = tc.instantiate("montithings.generator.cd2cpp.AssociationHelper")>
<#assign TypeHelper = tc.instantiate("montithings.generator.cd2cpp.TypeHelper", [package])>
#pragma once

<#assign typeName = type.getName()>
<#if existsHwc>
    <#assign typeName = typeName + "TOP">
</#if>

<#function java2cppTypeString type>
  <#assign output = type>
  <#assign output = output?replace("([^<]*)\\[]", "std::vector<$1>")>
  <#assign output = output?replace("String", "std::string")>
  <#assign output = output?replace("Integer", "int")>
  <#assign output = output?replace("Map", "std::map")>
  <#assign output = output?replace("Set", "std::set")>
  <#assign output = output?replace("List", "std::list")>
  <#assign output = output?replace("Boolean", "bool")>
  <#assign output = output?replace("Character", "char")>
  <#assign output = output?replace("Double", "double")>
  <#assign output = output?replace("Float", "float")>
  <#assign output = output?replace("InPort", "PortLink")>
  <#assign output = output?replace("OutPort", "PortLink")>
  <#return output>
</#function>

#include ${"<vector>"}
#include ${"<cereal/types/vector.hpp>"}
#include ${"<cereal/types/tloptional.hpp>"}
#include ${"<algorithm>"}
<#if type.isIsClass()>
  <#list type.getFieldList() as field>
    <#if !TypeHelper.isPrimitive(field.getType().getTypeInfo())>
      <#assign fieldType = java2cppTypeString(field.getType().getTypeInfo().getName())>
      #include "${fieldType}.h"
    </#if>
  </#list>
</#if>
<#list associations as assoc>
  <#assign t=AssociationHelper.getOtherSideTypeName(assoc, type)>
  #include "${t}.h"
</#list>
#include "tl/optional.hpp"

<#list imports as import>
#include "${import}"
</#list>

namespace ${package}
{

${kind} ${typeName} <#if super != "">: ${super} </#if>{

  <#if type.isIsEnum()>
    <#-- enum -->
    <#list type.getFieldList() as field>
        ${field.getName()}
        <#if !field?is_last>,</#if>
    </#list>

  <#elseif type.isIsClass()>
    <#-- class -->

    <#-- mandatoryFields are those required in the constructor -->
    <#-- They may originate from attributes or associations with cardinality [1] -->
    <#assign mandatoryFields = []>
    
    <#list type.getFieldList() as field>
      <#-- attributes -->
      <#assign fieldType = java2cppTypeString(field.getType().getTypeInfo().getName())>
      <#assign mandatoryFields = mandatoryFields + [{"name": field.getName(), "type":fieldType}]>
      protected: ${fieldType} ${field.getName()};
      public: ${fieldType} get${field.getName()?cap_first}() {
        return ${field.getName()};
      }
      public: void set${field.getName()?cap_first}(${fieldType} ${field.getName()}) {
        this->${field.getName()} = ${field.getName()};
      }
    </#list>

    <#-- associations -->
    <#list associations as assoc>
      <#-- TODO: Fix types printing -->
      <#-- <#assign t=typeHelper.printType(assoc.getTargetType().getLoadedSymbol())> -->
      <#assign t=AssociationHelper.getOtherSideTypeName(assoc, type)>
      <#assign n=AssociationHelper.getDerivedName(assoc, type)>

      <#if AssociationHelper.getOtherSideCardinality(assoc, type).isMult() >
      <#-- [*] ASTCDCardMult -->

        protected: std::vector<${t}> ${n};
        public: void set${n?cap_first}(std::vector<${t}> ${n}){
        this->${n} = ${n};
        }
        public: std::vector<${t}> get${n?cap_first}(){
        return this->${n};
        }
        public: void add${n?cap_first}(${t} ${n}){
        this->${n}.push_back(${n});
        }
        public: void remove${n?cap_first}(${t} ${n}){
        this->${n}.erase(std::remove(this->${n}.begin(), this->${n}.end(), ${n}), this->${n}.end());
        }

      <#elseif AssociationHelper.getOtherSideCardinality(assoc, type).isOpt() >
      <#-- [0..1] ASTCDCardOpt -->

        <#assign tOpt="tl::optional<"+t+">">
        protected: ${tOpt} ${n} = tl::nullopt;
        public: ${tOpt} get${n?cap_first}() {
        return ${n};
        }
        public: void set${n?cap_first}(${t} ${n}) {
        this->${n} = ${n};
        }

      <#else>
      <#-- [1] ASTCDCardOne -->

        <#assign mandatoryFields = mandatoryFields + [{"name": n, "type": t}]>
        protected: ${t} ${n};
        public: ${t} get${n?cap_first}() {
        return ${n};
        }
        public: void set${n?cap_first}(${t} ${n}) {
        this->${n} = ${n};
        }
      </#if>
    </#list> <#-- /associations -->

    <#-- equality operators -->
    public:
    bool
    operator== (const ${typeName} &rhs) const
    {
    return
    <#list type.getFieldList() as field>
        ${field.getName()} == rhs.${field.getName()} <#sep>&&</#sep>
    </#list>
    <#if type.getFieldList()?size != 0 && associations?size != 0>&&</#if>
    <#list associations as assoc>
        <#assign otherSide = AssociationHelper.getDerivedName(assoc, type)>
        ${otherSide} == rhs.${otherSide} <#sep>&&</#sep>
    </#list>
    <#if type.getFieldList()?size == 0 && associations?size == 0>true</#if>
    ;
    }
    public:
    bool
    operator!= (const ${typeName} &rhs) const
    {
    return !(rhs == *this);
    }

    <#-- constructor -->
    public: ${typeName}(
    <#list mandatoryFields as mandatoryField>
        ${java2cppTypeString(mandatoryField.type)} ${mandatoryField.name}
        <#if !mandatoryField?is_last>,</#if>
    </#list>
    ){
       <#list mandatoryFields as mandatoryField>
         this->${mandatoryField.name} = ${mandatoryField.name};
       </#list>
    }

    <#-- stream operator -->
    <#assign thisVar = typeName?uncap_first>
    friend std::ostream &
    operator<< (std::ostream &os, const ${typeName} &${thisVar})
    {
    os << "{ ";
    <#-- attributes -->
    <#list type.getFieldList() as field>
      os << "\"${field.getName()}\": \"" << ${thisVar}.${field.getName()} << "\"";
    </#list>

    <#-- associations -->
    <#list associations as assoc>
      <#assign n=AssociationHelper.getDerivedName(assoc, type)>
      <#if AssociationHelper.getOtherSideCardinality(assoc, type).isMult() >
        <#-- [*] ASTCDCardMult -->
        {
        os << "\"${n}\": [";
        bool isFirst = true;
        for (auto entry : ${thisVar}.${n}) {
          if (!isFirst) {os << ", ";}
          os << entry;
          isFirst = false;
        }
        os << "]";
        }
      <#elseif AssociationHelper.getOtherSideCardinality(assoc, type).isOpt() >
        <#-- [0..1] ASTCDCardOpt -->
        if (${thisVar}.${n}.has_value()) {
          os << "\"${n}\": \"" << ${thisVar}.${n}.value() << "\"";
        } else
        {
          os << "\"${n}\": {}";
        }
      <#else>
        <#-- [1] ASTCDCardOne -->
        os << "\"${n}\": \"" << ${thisVar}.${n} << "\"";
      </#if>
    </#list> <#-- /associations -->
    os << " }";
    return os;
    }

    <#-- serialization -->
    public:
    template <class Archive>
    void serialize( Archive & ar )
    {
    ar(
    <#list type.getFieldList() as field>
      ${field.getName()}<#sep>,</#sep>
    </#list>
    <#if type.getFieldList()?size gt 0 && associations?size gt 0>,</#if>
    <#list associations as assoc>
      <#assign n=AssociationHelper.getDerivedName(assoc, type)>
      ${n}<#sep>,</#sep>
    </#list>
    );
    }
    
    <#-- no-args constructor, if any arguments are present -->
    <#if mandatoryFields?size != 0>
    public: ${typeName}() {
    }
    </#if>   
    
  </#if><#-- /class -->
};

<#-- close namespaces -->
<#list 1..namespaceCount as i>
}
</#list>

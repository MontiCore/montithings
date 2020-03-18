<#-- (c) https://github.com/MontiCore/monticore -->
// (c) https://github.com/MontiCore/monticore
${tc.signature("package", "kind", "type", "super", "typeHelper", "imports")}
#pragma once

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
  <#return output>
</#function>

#include ${"<vector>"}
#include ${"<algorithm>"}
#include "tl/optional.hpp"
#include "Package.h"
<#list imports as import>
#include "${import}"
</#list>

namespace ${package} {

${kind} ${type.getName()} <#if super != "">: ${super} </#if>{

  <#if type.isEnum()>
    <#-- enum -->
    <#list type.getFields() as field>
        ${field.getName()}
        <#if !field?is_last>,</#if>
    </#list>

  <#elseif type.isClass()>
    <#-- class -->

    <#-- mandatoryFields are those required in the constructor -->
    <#-- They may originate from attributes or associations with cardinality [1] -->
    <#assign mandatoryFields = []>
    
    <#list type.getFields() as field>
        <#-- attributes -->
        <#assign mandatoryFields = mandatoryFields + [{"name": field.getName(), "type":field.getType().getStringRepresentation()}]>
        private: ${java2cppTypeString(field.getType().getStringRepresentation())} ${field.getName()};
        public: ${java2cppTypeString(field.getType().getStringRepresentation())} get${field.getName()?cap_first}() {
          return ${field.getName()};
        }
        public: void set${field.getName()?cap_first}(${java2cppTypeString(field.getType().getStringRepresentation())} ${field.getName()}) {
          this->${field.getName()} = ${field.getName()};
        }
    </#list>

    <#-- associations -->
    <#list type.getAssociations() as assoc>
      <#assign t=typeHelper.printType(assoc.getTargetType())>
      <#assign n=assoc.getDerivedName()>

      <#if assoc.getTargetCardinality().isMultiple()>
        <#-- [*] -->

        private: std::vector<${t}> ${n};
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

      <#elseif assoc.getTargetCardinality().getMin()==0>
        <#-- [0..1]-->

        <#assign tOpt="tl::optional<"+t+">">
        private: ${tOpt} ${n} = tl::nullopt;
        public: ${tOpt} get${n?cap_first}() {
          return ${n};
        }
        public: void set${n?cap_first}(${t} ${n}) {
          this->${n} = ${n};
        }

      <#else>
        <#-- [1] -->

        <#assign mandatoryFields = mandatoryFields + [{"name": n, "type": t}]>
        private: ${t} ${n};
        public: ${t} get${n?cap_first}() {
          return ${n};
        }
        public: void set${n?cap_first}(${t} ${n}) {
          this->${n} = ${n};
        }
      </#if>
    </#list> <#-- /associations -->

    <#-- constructor -->
    public: ${type.getName()}(
    <#list mandatoryFields as mandatoryField>
        ${java2cppTypeString(mandatoryField.type)} ${mandatoryField.name}
        <#if !mandatoryField?is_last>,</#if>
    </#list>
    ){
       <#list mandatoryFields as mandatoryField>
         this->${mandatoryField.name} = ${mandatoryField.name};
       </#list>
    }
    
    <#-- no-args constructor, if any arguments are present -->
    <#if mandatoryFields?size != 0>
    public: ${type.getName()}() {
    }
    </#if>   
    
  </#if><#-- /class -->
};

}
<#macro printStruct mainComp mainCompName package>
struct ${mainComp.getName()}Test : testing::Test
{
  ${package}${mainComp.getName()} *cmp${mainCompName};
    <#list mainComp.getSubComponents() as component>

  ${package}${component.getType().getName()} *${component.getName()}Cmp;
  ${package}${component.getType().getName()}Impl *${component.getName()}Impl;
  ${package}${component.getType().getName()}State *${component.getName()}State;
    </#list>

  ${mainComp.getName()}Test ()
  {
    cmp${mainCompName} = new ${package}${mainComp.getName()} ("${mainComp.getFullName()}");
    <#list mainComp.getSubComponents() as component>

  ${component.getName()}Cmp = cmp${mainCompName}->getSubcomp__${component.getName()?cap_first}();
    ${component.getName()}Impl = ${component.getName()}Cmp->getImpl();
    ${component.getName()}State = ${component.getName()}Cmp->getState();
    </#list>
  }

  ~${mainComp.getName()}Test ()
  {
    delete cmp${mainCompName};
  }
};
</#macro>

<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packages")}
<#list packages as package>
    } // namespace ${package}
</#list>
} // namespace montithings
${tc.signature("packages")}
namespace montithings {
<#list packages as package>
    namespace ${package} {
</#list>
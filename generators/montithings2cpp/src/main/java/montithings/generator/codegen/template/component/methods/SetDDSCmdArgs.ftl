<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void
${className}${Utils.printFormalTypeParameters(comp)}::setDDSCmdArgs (int c, char *a[]) {
            argc = c;
            // TODO: should be destroyed in destructor
            argv = new char[argc];
            argv = *a;
}
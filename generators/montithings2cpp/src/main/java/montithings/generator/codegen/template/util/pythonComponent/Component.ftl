<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("componentName")}

from ${componentName}Impl import ${componentName}Impl

if __name__=="__main__":
    connector = ${componentName}Impl()
    # Block forever
    connector.connect()
<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("targetName")}
target_link_libraries(${targetName} nng pthread curl ${r"${ATOMIC_LIBRARY}"})
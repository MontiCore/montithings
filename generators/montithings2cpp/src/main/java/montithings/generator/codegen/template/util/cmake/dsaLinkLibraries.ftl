<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("targetName")}
target_link_libraries(${r"${targetName}"} nng pthread curl ${r"${ATOMIC_LIBRARY}"})
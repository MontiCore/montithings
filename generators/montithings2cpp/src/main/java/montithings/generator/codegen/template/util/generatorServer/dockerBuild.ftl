${tc.signature("config","what")}
<#include "/template/Preamble.ftl">
# (c) https://github.com/MontiCore/monticore
cd ../../..
docker build . --file target/generated-sources/generator-server/Dockerfile --network host --tag ${config.getMainComponent()?lower_case}.generator-server:latest

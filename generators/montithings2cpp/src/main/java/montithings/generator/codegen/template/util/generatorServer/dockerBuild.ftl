${tc.signature("config","existsHWC")}
<#include "/template/Preamble.ftl">
# (c) https://github.com/MontiCore/monticore
#
# GENERATED FILE. DO NOT EDIT. CHANGES WILL BE OVERWRITTEN!
#
cd ../../..
docker build . --file target/generated-sources/generator-server/Dockerfile --network host --tag ${config.getMainComponent()?lower_case}.generator-server:latest

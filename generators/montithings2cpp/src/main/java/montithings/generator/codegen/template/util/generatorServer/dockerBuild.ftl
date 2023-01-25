${tc.signature("what")}
<#include "/template/Preamble.ftl">
# (c) https://github.com/MontiCore/monticore
cd ../../..
docker build . --file target/generated-sources/generator-server/Dockerfile --network host --tag generator-server:latest

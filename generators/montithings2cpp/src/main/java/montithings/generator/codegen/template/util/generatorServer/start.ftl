# (c) https://github.com/MontiCore/monticore
${tc.signature("port","what")}
<#include "/template/Preamble.ftl">

docker run --rm --net=host --cap-add=NET_ADMIN -p ${port}:${port} generator-server
${tc.signature("config","instanceNames","what")}
<#include "/template/Preamble.ftl">


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title> Montithings-DSL-Index </title>
</head>
<body>
    <#list instanceNames as instance>
        <p><a href="/${instance}">${instance}</a></p>
    </#list>
    
</body>
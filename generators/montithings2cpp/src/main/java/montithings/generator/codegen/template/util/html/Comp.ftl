${tc.signature("config","name","what")}
<#include "/template/Preamble.ftl">


<!DOCTYPE HTML>
<html>
<head>
    <title> Montithings-DSL-Upload(${name}) </title>
</head>
<body>
    <form action="/${name}" method="POST" enctype="multipart/form-data"  target="frame">
        <div><label>Model:</label>
        <input type="file" name="fileUpload"></div>
        <div><input type="submit" /></div>
    </form>
    <p><a href="/">Index</a></p>
    <iframe name="frame" style="display:none;"></iframe>
</body>
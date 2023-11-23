<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config","instanceNames","explain","existsHWC")}
<#include "/template/Preamble.ftl">


<!DOCTYPE HTML>
<html lang="en">
    <head>
        <title> Montithings-DSL-Index </title>
        <style>
            * {
                margin: 0;
                padding: 0;
                border: 0;
                outline: 0;
                font-size: 100%;
                vertical-align: baseline;
                background: transparent;
                color: rgb(36, 34, 34);
            }
            .grid-container {
                display: grid;
                height: 100vh;
                width: 100vw;
                gap: 0px;
                grid-template-columns:  auto 70vw;
                grid-template-rows: 5vh auto;
            }

            .explain {
                background-color: whitesmoke; 
                grid-column: 2 / 3;
                grid-row: 2 / 3;
                border-left: 1px solid rgb(206, 201, 201);
                
            }
            .explain-cont {
                height: 100%;
                width: 100%;
            }

            .links {
                background-color: whitesmoke; 
                grid-column: 1 / 2;
                grid-row: 2 / 3;
                padding: 2vh;;
            }

            .links-cont {
                height: 100%;
                width: 100%;
            }

            .header {
                background-color: whitesmoke;
                grid-column: 1 / 3;
                grid-row: 1 / 2;
                padding-left: 1vh;
                border-bottom: 1px solid rgb(206, 201, 201);
                
            }
            .header-cont {
                font-size: 4vh;
                margin:0 auto;    
                display:block;
                line-height: 5vh;
                color: rgb(36, 34, 34);
            }

            a { color: inherit; }

        </style>   
    </head>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>

    <body>


        <div class="grid-container">
            <div class="explain">
                <iframe class="explain-cont" srcdoc="${explain}"></iframe>
            </div>
            <div class="header">
                <div class="header-cont">
                    DSL Component Overview
                </div>
            </div>
            <div class="links">
                <#list instanceNames as instanceName>
                <button type="button" class="btn btn-outline-primary btn-lg btn-block" onclick="window.location.href='/${instanceName}';">${instanceName}</button>
                </#list>
            </div>
        </div> 
        
    </body>
</html>
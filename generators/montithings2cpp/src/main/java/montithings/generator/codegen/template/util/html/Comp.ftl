${tc.signature("config","name","explain","what")}
<#include "/template/Preamble.ftl">

<!DOCTYPE HTML>
<html lang="en">
    <head>
        <title> Montithings-DSL-Upload(${name}) </title>
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
                grid-template-columns: 5vh auto 50vw;
                grid-template-rows: 5vh auto 33vh;
            }

            .explain {
                background-color: whitesmoke; 
                grid-column: 3 / 4;
                grid-row: 2 / 4;
                border-left: 1px solid rgb(206, 201, 201);
                
            }
            .explain-cont {
                height: 100%;
                width: 100%;
            }

            .form {
                background-color: whitesmoke; 
                grid-column: 1 / 3;
                grid-row: 2 / 3;
                padding: 2vh;;
            }

            .form-cont {
                height: 100%;
                width: 100%;
            }

            .cmd {
                background-color: rgb(36, 34, 34);
                grid-column: 1 / 3;
                grid-row: 3 / 4;
            }

            .cmd-cont {
                height: 100%;
                width: 100%;
                color: whitesmoke;

            }

            .index {
                background-color: whitesmoke;
                grid-column: 1 / 2;
                grid-row: 1 / 2;
                border-bottom: 1px solid rgb(206, 201, 201);
            }
            .index-cont {
                font-size: 6vh;
                margin:0 auto;    
                display:block;
                text-align: center;
                line-height: 3.6vh;
                color: rgb(36, 34, 34);
            }

            .index-cont:hover {
                font-size: 6.2vh;
                margin:0 auto;    
                display:block;
                text-align: center;
                line-height: 3.6vh;
                color: rgb(36, 34, 34);
            }


            .header {
                background-color: whitesmoke;
                grid-column: 2 / 4;
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

            .custom-file-input::-webkit-file-upload-button {
                visibility: hidden;
            }
            .custom-file-input::before {
                content: 'Select some files';
                display: inline-block;
                background: -webkit-linear-gradient(top, #f9f9f9, #e3e3e3);
                border: 1px solid #999;
                border-radius: 3px;
                padding: 5px 8px;
                outline: none;
                white-space: nowrap;
                -webkit-user-select: none;
                cursor: pointer;
                text-shadow: 1px 1px #fff;
                font-weight: 700;
                font-size: 10pt;
            }
            .custom-file-input:hover::before {
                border-color: black;
            }
            .custom-file-input:active::before {
                background: -webkit-linear-gradient(top, #e3e3e3, #f9f9f9);
            }


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
                    DSL Upload: ${name}
                </div>
            </div>
            <div class="index">
                <a class="index-cont" style="text-decoration: none;" href="/">&#8962</a>
            </div>
            <div class="cmd">
                <iframe class="cmd-cont" name="output" srcdoc="<p style=&quot;color: whitesmoke&quot;>This pannel will show you inforamtions aubout your requests...</p>"></iframe>
            </div>
            <div class="form">
                <form class="form-cont" action="/${name}" method="POST" enctype="multipart/form-data"  target="output">
                    <div class="mb-3">
                        <label for="formFileSm" class="form-label">Upload your language file and click submit to update your components behavior.</label>
                        <input class="form-control form-control-sm" id="formFileSm" type="file" name="fileUpload">
                    </div>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div> 
        
    </body>
</html>
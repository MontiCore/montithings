<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config","className")}
<#include "/template/component/helper/GeneralPreamble.ftl">

${Utils.printTemplateArguments(comp)}
void ${className}${Utils.printFormalTypeParameters(comp)}::website_hoster(){
  
  httplib::Server svr;

  svr.Get("/", [](const httplib::Request &, httplib::Response &res) {
    std::ifstream t("html/Index.html");
    std::stringstream buffer;
    buffer << t.rdbuf();
    std::string cont = buffer.str();
    res.set_content(cont, "text/html");
  });
  <#list ComponentHelper.getAllLanguageDirectories(config) as file>
    svr.Get("${file}", [](const httplib::Request &, httplib::Response &res) {
    std::ifstream t("html${file}.html");
    std::stringstream buffer;
    buffer << t.rdbuf();
    std::string cont = buffer.str();
    res.set_content(cont, "text/html");
  });
  svr.Post("${file}", [&](const httplib::Request &req, httplib::Response &res) {
    //store model
    const auto& file = req.get_file_value("fileUpload");
    std::fstream modelFile;
    modelFile.open("models${file}/model.mc",std::fstream::out);
    modelFile << file.content;
    modelFile.close();
    res.set_content("","text/html");

    //generate py

    //send py
    httplib::Client cli("127.0.0.1:8081");
    std::ifstream pyFile("models${file}/code.py");
    std::stringstream buffer;
    buffer << pyFile.rdbuf();
    std::string cont = buffer.str();
    httplib::Params params;
    params.emplace("fileUpload", cont);
    cli.Post("/Py", params);
  });
  </#list>

  svr.listen("0.0.0.0", 8080);
}
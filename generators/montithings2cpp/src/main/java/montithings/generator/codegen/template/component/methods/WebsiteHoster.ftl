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
    res.set_content("","text/html");

    //generate py
    httplib::Client cliGen("127.0.0.1:5004");
    httplib::Params paramsGen;
    paramsGen.emplace("fileUpload", file.content);
    httplib::Result response = cliGen.Post("${file}", paramsGen);

    //send py
    httplib::Client cli("127.0.0.1:8081");
    httplib::Params paramsPy;
    paramsPy.emplace("fileUpload", response.value().body);
    cli.Post("/Py", paramsPy);
  });
  </#list>

  svr.listen("0.0.0.0", 8080);
}
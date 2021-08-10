<#macro printDelay componentHelper prettyPrinter sD4CElement>
  // delay
  LOG(INFO) << "${prettyPrinter.prettyprint(sD4CElement)?replace("\n", "")?replace("\r", "")}";
  std::this_thread::sleep_for(std::chrono::${componentHelper.printTime(sD4CElement.getSIUnitLiteral())});

</#macro>

// (c) https://github.com/MontiCore/monticore
package montithings.generator

info("--------------------------------")
info("MT Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Language path     : " + languagePath)
debug("Test path      : " + testPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("HWC dir        : " + handwrittenCode)
debug("Platform       : " + _configuration.configParams.targetPlatform)
debug("--------------------------------")
generate(modelPath, out, handwrittenCode, testPath, _configuration.configParams, languagePath)
info("--------------------------------")
info("MT Generator END")
info("--------------------------------")
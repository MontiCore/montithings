// (c) https://github.com/MontiCore/monticore
package montithings.generator

info("--------------------------------")
info("MT Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Test path     : " + testPath)
debug("Output dir     : " + outputDir.getAbsolutePath())
debug("HWC dir        : " + handcodedPath)
debug("Platform       : " + _configuration.configParams.targetPlatform)
debug("--------------------------------")
generate(modelPath, outputDir, handcodedPath, testPath, _configuration.configParams)
info("--------------------------------")
info("MT Generator END")
info("--------------------------------")
// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp

info("--------------------------------")
info("CD2CPP Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Hwc path       : " + hwcPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, hwcPath, out)
info("--------------------------------")
info("CD2CPP Generator END")
info("--------------------------------")

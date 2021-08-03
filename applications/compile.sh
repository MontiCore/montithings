# (c) https://github.com/MontiCore/monticore
set -e

buildProject() {
  cd "$1"/target/generated-sources
  ./build.sh "$2"
  cd -
}

buildProject "basic-input-output" "hierarchy"
buildProject "hierarchy" "hierarchy"
buildProject "mqtt-ports" "hierarchy.Example"
buildProject "retain-state" "hierarchy"
buildProject "sensor-actuator-access" "hierarchy.Example"

cd language-features

buildProject "behavior" "hierarchy"
buildProject "class-diagrams" "hierarchy"
buildProject "interface-components" "hierarchy"
buildProject "interface-componentsMTB" "hierarchy"
buildProject "ocl" "hierarchy"
buildProject "pre-postconditions" "hierarchy"
buildProject "si-units" "hierarchy"
buildProject "statecharts" "hierarchy"
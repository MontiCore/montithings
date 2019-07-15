/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.tagging.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by MichaelvonWenckstern on 28.06.2016.
 */
public class UnitKinds {
  protected static UnitKinds instance = null;

  protected UnitKinds() {
    createHashMap();
  }

  protected static UnitKinds getInstance() {
    if (instance == null) {
      instance = new UnitKinds();
    }
    return instance;
  }

  public static boolean contains(String unitKind) {
    return getInstance().quantities.contains(unitKind);
  }

  @Override
  public String toString() {
    return quantities.stream().collect(Collectors.joining(", "));
  }

  public static String available() {
    return getInstance().toString();
  }

  protected HashSet<String> quantities;

  protected void createHashMap() {
    // copied from http://jscience.org/api/javax/measure/quantity/Quantity.html
    quantities = new LinkedHashSet<>(Arrays.asList(
        "Acceleration",
        "AmountOfSubstance",
        "Angle",
        "AngularAcceleration",
        "AngularVelocity",
        "Area",
        "CatalyticActivity",
        "DataAmount",
        "DataRate",
        "Dimensionless",
        "Duration",
        "DynamicViscosity",
        "ElectricCapacitance",
        "ElectricCharge",
        "ElectricConductance",
        "ElectricCurrent",
        "ElectricInductance",
        "ElectricPotential",
        "ElectricResistance",
        "Energy",
        "Force",
        "Frequency",
        "Illuminance",
        "KinematicViscosity",
        "Length",
        "LuminousFlux",
        "LuminousIntensity",
        "MagneticFlux",
        "MagneticFluxDensity",
        "Mass",
        "MassFlowRate",
        "Money",
        "Power",
        "Pressure",
        "RadiationDoseAbsorbed",
        "RadiationDoseEffective",
        "RadioactiveActivity",
        "SolidAngle",
        "Temperature",
        "Torque",
        "Velocity",
        "Volume",
        "VolumetricDensity",
        "VolumetricFlowRate"
    ));
  }
}

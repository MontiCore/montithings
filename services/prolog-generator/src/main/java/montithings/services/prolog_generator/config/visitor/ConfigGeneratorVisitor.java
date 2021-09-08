// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator.config.visitor;

import montithings.services.prolog_generator.config._visitor.ConfigVisitor2;

public class ConfigGeneratorVisitor implements ConfigVisitor2 {
    ConfigVisitor2 realThis = this;

    public ConfigGeneratorVisitor() {
    }

    public ConfigVisitor2 getRealThis() {
        return realThis;
    }


    public void setRealThis(ConfigVisitor2 realThis) {
        this.realThis = realThis;
    }
    
}
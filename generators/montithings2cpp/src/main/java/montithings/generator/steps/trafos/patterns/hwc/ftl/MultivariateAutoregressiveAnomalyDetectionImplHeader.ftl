${tc.signature("compname", "windowSize", "tolerance")}
#pragma once
#include "${compname}ImplTOP.h"
#include "MultivariateAutoregressivyAnomalyDetection.h"

${Utils.printNamespaceStart(comp)}
    class ${compname}Impl : public ${compname}ImplTOP
    {

    private:
      MultivariateAutoregressivyAnomalyDetection *ad = new MultivariateAutoregressivyAnomalyDetection(${windowSize}, (float)${tolerance});

    public:
      using ${compname}ImplTOP::${compname}ImplTOP;
      ${compname}Result getInitialValues() override;
      ${compname}Result compute(${compname}Input input) override;
    };
${Utils.printNamespaceEnd(comp)}
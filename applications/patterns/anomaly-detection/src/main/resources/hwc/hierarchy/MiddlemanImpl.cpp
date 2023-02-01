#include "MiddlemanImpl.h"

namespace montithings
{
  namespace hierarchy
  {

    MiddlemanResult MiddlemanImpl::getInitialValues()
    {
      return {};
    }

    MiddlemanResult MiddlemanImpl::compute(MiddlemanInput input)
    {
      MiddlemanResult result;

      if (input.getInput1())
      {
        state.postSetRunningIndex(state.getRunningIndex() + 1);
        log(concat("Middleman Input 1: ", input.getInput1().value()));
        if (state.getRunningIndex() == 3)
        {
          // Manually produce anomaly
          result.setOutput(10000);
          interface.getPortOutput()->setNextValue(result.getOutputMessage());
        }
        else
        {
          result.setOutput(input.getInput1().value());
          interface.getPortOutput()->setNextValue(result.getOutputMessage());
        }
      }

      if (input.getInput2())
      {
        log(concat("Middleman Input 2: ", input.getInput2().value()));
      }

      return result;
    }

  } // namespace hierarchy
} // namespace montithings

${tc.signature("packageName", "compname", "namesOfInputPorts", "namesOfOutputPorts", "pgPortTypes", "tablename")}
#include "${compname}Impl.h"

namespace montithings {
    namespace ${packageName} {
        ${compname}Result
        ${compname}Impl::getInitialValues()
        {
          return {};
        }

        ${compname}Result
        ${compname}Impl::compute(${compname}Input input)
        {
          ${compname}Result result;

          std::string connectionStr = std::getenv("connectionStr");

          <#list 0..namesOfInputPorts?size-1 as i>
          if (input.get${namesOfInputPorts[i]?cap_first}()) {
            // 1. Insert data in postgres db
            PostgresClient *pgClient = new PostgresClient();

            std::stringstream query;
            query << "CREATE SCHEMA IF NOT EXISTS public; "
                  << "CREATE TABLE IF NOT EXISTS public.${tablename}${i} (id serial NOT NULL, "
                     "timestamp timestamp NOT NULL DEFAULT now(), data ${pgPortTypes[i]} NOT NULL, "
                     "CONSTRAINT ${tablename}${i}pk PRIMARY KEY (id)); "
                  << "INSERT INTO public.${tablename}${i} (data) VALUES ('" << input.get${namesOfInputPorts[i]?cap_first}().value () << "');";

            pgClient->exec (query.str (), connectionStr);

            // 2. Pass values through
            result.set${namesOfOutputPorts[i]?cap_first}(input.get${namesOfInputPorts[i]?cap_first}().value());
            interface.getPort${namesOfOutputPorts[i]?cap_first}()->setNextValue(result.get${namesOfOutputPorts[i]?cap_first}Message());
          }
          </#list>

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings
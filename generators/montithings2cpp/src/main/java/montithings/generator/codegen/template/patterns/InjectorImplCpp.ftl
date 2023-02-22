${tc.signature("packageName", "compname", "pgPortType")}
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

          if (input.getIn()) {
            // 1. Insert data in postgres db
            PostgresClient *pgClient = new PostgresClient();

            std::stringstream query;
            query << "CREATE SCHEMA IF NOT EXISTS public; "
                  << "CREATE TABLE IF NOT EXISTS public.${compname} (id serial NOT NULL, "
                     "timestamp timestamp NOT NULL DEFAULT now(), data ${pgPortType} NOT NULL, "
                     "CONSTRAINT ${compname}_pk PRIMARY KEY (id)); "
                  << "INSERT INTO public.${compname} (data) VALUES (" << input.getIn ().value () << ");";

            pgClient->exec (query.str (), connectionStr);

            // 2. Pass values through
            result.setOut(input.getIn().value());
            interface.getPortOut()->setNextValue(result.getOutMessage());
          }

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings
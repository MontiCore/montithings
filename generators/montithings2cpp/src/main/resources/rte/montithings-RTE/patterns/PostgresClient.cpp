#include "PostgresClient.h"

/**
 * Connects to db using connection string and executes query
 */
void PostgresClient::exec(std::string query, std::string connectionStr)
{
  try
  {
    pqxx::connection con(connectionStr);
    pqxx::work client(con);
    client.exec(query);
    client.commit();
    con.disconnect();
  }
  catch (const std::exception &e)
  {
    std::cerr << e.what() << std::endl;
  }
}
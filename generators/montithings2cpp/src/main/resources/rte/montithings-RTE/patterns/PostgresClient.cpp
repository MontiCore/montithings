#include "PostgresClient.h"

/**
 * Connects to db using connection string and executes query
 */
void PostgresClient::exec(std::string query, std::string connectionStr)
{
  try
  {
    PGconn *conn = PQconnectdb(connectionStr.c_str());
    if (PQstatus(conn) != CONNECTION_OK)
    {
      fprintf(stderr, "Connection to database failed: %s", PQerrorMessage(conn));
      PQfinish(conn);
    }
    PGresult *res = PQexec(conn, query.c_str());
    if (PQresultStatus(res) != PGRES_COMMAND_OK)
    {
      fprintf(stderr, "Query failed failed: %s", PQerrorMessage(conn));
      PQclear(res);
      PQfinish(conn);
    }
    PQclear(res);
    PQfinish(conn);
  }
  catch (const std::exception &e)
  {
    std::cerr << e.what() << std::endl;
  }
}
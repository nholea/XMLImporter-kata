import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

  public Connection getPostgresConnection() throws SQLException {

    return DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres");
  }

}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;
import xmlmodels.Staff;

public class DataBaseActions {

  public void insertCompany(Company company) throws SQLException {
    try (Connection conn = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

      int companyId = getGeneratedKeyForCompany(company, conn);
      for (Staff staff : company.staff) {
        insertStaff(conn, companyId, staff);
        insertSalary(conn, staff);
      }
    }
  }

  private int getGeneratedKeyForCompany(Company company, Connection conn) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)",
      Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, company.name);
      preparedStatement.executeUpdate();

      return getCompanyId(preparedStatement);
    }
  }

  private void insertSalary(Connection conn, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement(
      "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setString(2, staff.salary.currency);
      preparedStatement.setInt(3, staff.salary.value);
      preparedStatement.executeUpdate();
    }
  }

  private void insertStaff(Connection conn, int companyId, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement(
      "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setInt(2, companyId);
      preparedStatement.setString(3, staff.firstname);
      preparedStatement.setString(4, staff.lastname);
      preparedStatement.setString(5, staff.nickname);
      preparedStatement.executeUpdate();
    }
  }

  private int getCompanyId(PreparedStatement preparedStatement) throws SQLException {
    int companyId;
    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
      if (generatedKeys.next()) {
        companyId = (int) generatedKeys.getLong(1);
      } else {
        throw new SQLException("No ID obtained.");
      }
    }
    return companyId;
  }
  
}

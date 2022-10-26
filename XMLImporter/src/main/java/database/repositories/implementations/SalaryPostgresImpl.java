package database.repositories.implementations;

import database.repositories.SalaryRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import xmlmodels.Staff;

public class SalaryPostgresImpl implements SalaryRepository {

  @Override
  public void insertSalary(Connection connection, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement(
      "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setString(2, staff.salary.currency);
      preparedStatement.setInt(3, staff.salary.value);
      preparedStatement.executeUpdate();
    }
  }

}

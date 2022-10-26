package database.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import xmlmodels.Staff;

public interface SalaryRepository {

  void insertSalary(Connection connection, Staff staff) throws SQLException;

}

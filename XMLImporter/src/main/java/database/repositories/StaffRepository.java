package database.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import xmlmodels.Staff;

public interface StaffRepository {

  void insertStaff(Connection connection, int companyId, Staff staff) throws SQLException;

}

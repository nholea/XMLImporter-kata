import java.sql.Connection;
import java.sql.SQLException;
import xmlmodels.Company;
import xmlmodels.Staff;

public class DataBaseActions {

  private final DataBaseConnection dataBaseConnection;
  private final CompanyWarehouse companyWarehouse;
  private final SalaryWarehouse salaryWarehouse;
  private final StaffWarehouse staffWarehouse;

  public DataBaseActions(DataBaseConnection dataBaseConnection, CompanyWarehouse companyWarehouse, SalaryWarehouse salaryWarehouse,
    StaffWarehouse staffWarehouse) {
    this.dataBaseConnection = dataBaseConnection;
    this.companyWarehouse = companyWarehouse;
    this.salaryWarehouse = salaryWarehouse;
    this.staffWarehouse = staffWarehouse;
  }

  public void insertCompany(Company company) throws SQLException {
    Connection connection = dataBaseConnection.getPostgresConnection();

    int companyId = companyWarehouse.insertCompanyNameAndGetGeneratedKeyAnd(company, connection);
    for (Staff staff : company.staff) {
      staffWarehouse.insertStaff(connection, companyId, staff);
      salaryWarehouse.insertSalary(connection, staff);
    }

  }

}

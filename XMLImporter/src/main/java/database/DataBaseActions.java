package database;

import database.repositories.CompanyRepository;
import database.repositories.SalaryRepository;
import database.repositories.StaffRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import xmlmodels.Company;
import xmlmodels.Staff;

public class DataBaseActions {


  private final CompanyRepository companyRepository;
  private final SalaryRepository salaryRepository;
  private final StaffRepository staffRepository;

  public DataBaseActions(CompanyRepository companyRepository, SalaryRepository salaryRepository,
    StaffRepository staffRepository) {

    this.companyRepository = companyRepository;
    this.salaryRepository = salaryRepository;
    this.staffRepository = staffRepository;
  }

  private Connection getPostgresConnection() throws SQLException {

    return DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres");
  }


  public void insertCompany(Company company) throws SQLException {
    Connection connection = getPostgresConnection();

    int companyId = companyRepository.insertCompanyNameAndGetGeneratedKeyAnd(company, connection);
    for (Staff staff : company.staff) {
      staffRepository.insertStaff(connection, companyId, staff);
      salaryRepository.insertSalary(connection, staff);
    }
  }
  
}

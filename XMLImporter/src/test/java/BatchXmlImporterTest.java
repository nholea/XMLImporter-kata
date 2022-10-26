import static org.assertj.core.api.Assertions.assertThat;

import converters.CompanyConverter;
import database.DataBaseActions;
import database.repositories.implementations.CompanyPostgresImpl;
import database.repositories.implementations.SalaryPostgresImpl;
import database.repositories.implementations.StaffPostgresImpl;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import xmlmodels.Company;
import xmlmodels.Salary;
import xmlmodels.Staff;

class BatchXmlImporterTest {


  final Path path = Path.of(
    System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources");

  @Test
  void importXmlIntoDatabase() throws JAXBException, IOException, SQLException {
    CompanyConverter companyConverter = new CompanyConverter();
    FileExtensionFinder fileExtensionFinder = new FileExtensionFinder();
    CompanyPostgresImpl companyPostgresImpl = new CompanyPostgresImpl();
    SalaryPostgresImpl salaryPostgresImpl = new SalaryPostgresImpl();
    StaffPostgresImpl staffPostgresImpl = new StaffPostgresImpl();
    DataBaseActions dataBaseActions = new DataBaseActions(companyPostgresImpl, salaryPostgresImpl, staffPostgresImpl);
    BatchXmlImporter batchXmlImporter = new BatchXmlImporter(fileExtensionFinder, companyConverter, dataBaseActions);
    clearTables();

    batchXmlImporter.importCompaniesFromXmlFiles(path);

    var companies = getAllCompanies();
    assertThat(companies).hasSize(2);
  }

  private void clearTables() throws SQLException {
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {
      try (PreparedStatement preparedStatement = connection.prepareStatement(
        "DELETE FROM salary; DELETE FROM staff; DELETE FROM company")) {
        preparedStatement.executeUpdate();
      }

    }
  }

  private List<Company> getAllCompanies() throws SQLException {
    ArrayList<Company> companies = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {
      getCompanies(companies, connection);
      getStaff(companies, connection);
      getSalary(companies, connection);
    }
    return companies;
  }

  private void getSalary(ArrayList<Company> companies, Connection connection) throws SQLException {
    for (Company company : companies) {
      for (Staff staff : company.staff) {
        var resultSet = connection.createStatement().executeQuery("SELECT * FROM salary WHERE staff_id = " + staff.id);
        while (resultSet.next()) {
          var salary = new Salary();
          salary.currency = resultSet.getString("currency");
          salary.value = resultSet.getInt("value");
          staff.salary = salary;
        }
      }
    }
  }

  private void getStaff(ArrayList<Company> companies, Connection connection) throws SQLException {
    for (Company company : companies) {
      var resultSet = connection.createStatement().executeQuery("SELECT * FROM staff WHERE company_id = " + company.id);
      while (resultSet.next()) {
        var staff = new Staff();
        staff.id = resultSet.getInt("id");
        staff.firstname = resultSet.getString("first_name");
        staff.lastname = resultSet.getString("last_name");
        staff.nickname = resultSet.getString("nick_name");
        company.staff.add(staff);
      }
    }
  }

  private void getCompanies(ArrayList<Company> companies, Connection connection) throws SQLException {
    var resultSet = connection.createStatement().executeQuery("SELECT * FROM company");
    while (resultSet.next()) {
      var company = new Company();
      company.id = resultSet.getInt("id");
      company.name = resultSet.getString("name");
      companies.add(company);
    }
  }
}

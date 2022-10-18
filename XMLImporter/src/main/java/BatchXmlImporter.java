import static java.nio.file.Files.walk;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import xmlmodels.Company;
import xmlmodels.Staff;

public class BatchXmlImporter {

  public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {
    List<Path> paths = findXmlFilePathExtension(folderPath);

    ArrayList<Company> companies = getParsedCompanies(paths);

    for (Company company : companies) {
      try (Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

        int companyId;
        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)",
          Statement.RETURN_GENERATED_KEYS)) {
          preparedStatement.setString(1, company.name);
          preparedStatement.executeUpdate();

          companyId = getCompanyId(preparedStatement);
        }

        for (Staff staff : company.staff) {
          insertStaff(conn, companyId, staff);
          insertSalary(conn, staff);
        }
      }
    }
  }

  private static void insertSalary(Connection conn, Staff staff) throws SQLException {
    try (PreparedStatement preparedStatement = conn.prepareStatement(
      "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
      preparedStatement.setInt(1, staff.id);
      preparedStatement.setString(2, staff.salary.currency);
      preparedStatement.setInt(3, staff.salary.value);
      preparedStatement.executeUpdate();
    }
  }

  private static void insertStaff(Connection conn, int companyId, Staff staff) throws SQLException {
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

  private static int getCompanyId(PreparedStatement preparedStatement) throws SQLException {
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

  private static ArrayList<Company> getParsedCompanies(List<Path> paths) throws JAXBException {
    ArrayList<Company> companies = new ArrayList<>();
    for (Path path : paths) {
      Company company = parseCompanyToJAXBContextFormat(path);
      companies.add(company);
    }
    return companies;
  }

  private static Company parseCompanyToJAXBContextFormat(Path path) throws JAXBException {
    File file = new File(path.toString());
    JAXBContext jaxbContext = JAXBContext.newInstance(Company.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    Company company = (Company) jaxbUnmarshaller.unmarshal(file);
    return company;
  }

  private static List<Path> findXmlFilePathExtension(Path folderPath) throws IOException {
    final String fileExtension = ".xml";
    List<Path> paths;
    try (Stream<Path> pathStream = walk(folderPath)
      .filter(Files::isRegularFile)
      .filter(filePath ->
        filePath.toString()
          .endsWith(fileExtension))) {
      paths = pathStream
        .collect(Collectors.toList());
    }
    return paths;
  }

}

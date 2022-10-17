import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import xmlmodels.Company;
import xmlmodels.Staff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

public class BatchXmlImporter {

    public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {
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
        ArrayList<Company> companies = new ArrayList<>();
        for (Path path : paths) {
            File file = new File(path.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(Company.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Company company = (Company) jaxbUnmarshaller.unmarshal(file);
            companies.add(company);
        }
        for (Company company : companies) {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")) {

                final int companyId;
                try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, company.name);
                    preparedStatement.executeUpdate();

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            companyId = (int) generatedKeys.getLong(1);
                        } else throw new SQLException("No ID obtained.");
                    }
                }

                for (Staff staff : company.staff) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(
                            "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")) {
                        preparedStatement.setInt(1, staff.id);
                        preparedStatement.setInt(2, companyId);
                        preparedStatement.setString(3, staff.firstname);
                        preparedStatement.setString(4, staff.lastname);
                        preparedStatement.setString(5, staff.nickname);
                        preparedStatement.executeUpdate();
                    }

                    try (PreparedStatement preparedStatement = conn.prepareStatement(
                            "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")) {
                        preparedStatement.setInt(1, staff.id);
                        preparedStatement.setString(2, staff.salary.currency);
                        preparedStatement.setInt(3, staff.salary.value);
                        preparedStatement.executeUpdate();
                    }
                }
            }
        }
    }

}

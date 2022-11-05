import converters.CompanyConverter;
import database.DataBaseActions;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import xmlmodels.Company;

public class BatchXmlImporter {

  private final FileExtensionFinder fileExtensionFinder;
  private final CompanyConverter companyConverter;
  private final DataBaseActions dataBaseActions;

  public BatchXmlImporter(FileExtensionFinder fileExtensionFinder, CompanyConverter companyConverter, DataBaseActions dataBaseActions) {
    this.fileExtensionFinder = fileExtensionFinder;
    this.companyConverter = companyConverter;
    this.dataBaseActions = dataBaseActions;
  }

  public void importCompaniesFromXmlFiles(Path folderPath) throws IOException, JAXBException, SQLException {
    List<Path> paths = fileExtensionFinder.findPathsWithExtension(folderPath, ".xml");
    ArrayList<Company> companies = getCompaniesConvertedFrom(paths);
    insertCompaniesIntoDatabase(companies);
  }

  private ArrayList<Company> getCompaniesConvertedFrom(List<Path> paths) throws JAXBException {
    return (ArrayList<Company>) paths.stream().map(path -> {
      try {
        return companyConverter.companyToJAXBContextFormat(path);
      } catch (JAXBException e) {
        throw new RuntimeException(e);
      }

    }).collect(Collectors.toList());

  }

  private void insertCompaniesIntoDatabase(ArrayList<Company> companies) throws SQLException {
    for (Company company : companies) {
      dataBaseActions.insertCompany(company);
    }
  }

}

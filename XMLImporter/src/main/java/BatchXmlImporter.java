import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    List<Path> paths = fileExtensionFinder.findXmlPaths(folderPath);
    ArrayList<Company> companies = getParsedCompanies(paths);
    for (Company company : companies) {
      dataBaseActions.insertCompany(company);
    }
  }

  private ArrayList<Company> getParsedCompanies(List<Path> paths) throws JAXBException {
    ArrayList<Company> companies = new ArrayList<>();
    for (Path path : paths) {
      Company company = companyConverter.parseCompanyToJAXBContextFormat(path);
      companies.add(company);
    }
    return companies;
  }

}

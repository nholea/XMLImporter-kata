import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import xmlmodels.Company;

public class BatchXmlImporter {

  private final FileExtensionFinder fileExtensionFinder;
  private final DataParsing dataParsing;

  private final DataBaseActions dataBaseActions;

  public BatchXmlImporter(FileExtensionFinder fileExtensionFinder, DataParsing dataParsing, DataBaseActions dataBaseActions) {
    this.fileExtensionFinder = fileExtensionFinder;
    this.dataParsing = dataParsing;
    this.dataBaseActions = dataBaseActions;
  }


  public void importFiles(Path folderPath) throws IOException, JAXBException, SQLException {
    List<Path> paths = fileExtensionFinder.findXmlPaths(folderPath);
    ArrayList<Company> companies = getParsedCompanies(paths);
    for (Company company : companies) {
      dataBaseActions.insertCompany(company);
    }
  }

  private ArrayList<Company> getParsedCompanies(List<Path> paths) throws JAXBException {
    ArrayList<Company> companies = new ArrayList<>();
    for (Path path : paths) {
      Company company = dataParsing.parseCompanyToJAXBContextFormat(path);
      companies.add(company);
    }
    return companies;
  }

}

package converters;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;
import xmlmodels.Company;

public class CompanyConverter {

  public Company companyToJAXBContextFormat(Path path) throws JAXBException {
    File file = new File(path.toString());
    JAXBContext jaxbContext = JAXBContext.newInstance(Company.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    return (Company) jaxbUnmarshaller.unmarshal(file);
  }

}

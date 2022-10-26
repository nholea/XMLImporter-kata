package database.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import xmlmodels.Company;

public interface CompanyRepository {

  int insertCompanyNameAndGetGeneratedKey(Company company, Connection connection) throws SQLException;

}

package database.repositories.implementations;

import database.repositories.CompanyRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import xmlmodels.Company;

public class CompanyPostgresImpl implements CompanyRepository {

  @Override
  public int insertCompanyAndGetGeneratedKey(Company company, Connection connection) throws SQLException {
    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO company(name) VALUES (?)",
      Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, company.name);
      preparedStatement.executeUpdate();

      return getCompanyId(preparedStatement);
    }
  }

  private int getCompanyId(PreparedStatement preparedStatement) throws SQLException {
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

}

package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class MySqlBaseDAO {

	public MySqlBaseDAO() {
		try {
			configureDatabase();
		} catch (Exception e) {
			throw new RuntimeException("Failed to configure database", e);
		}
	}

	private void configureDatabase() throws DataAccessException {
		DatabaseManager.createDatabase();
		try (Connection conn = DatabaseManager.getConnection()) {
			for (String statement : getCreateStatements()) {
				try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException ex) {
			throw new DataAccessException("Unable to configure database: " + ex.getMessage());
		}
	}

	protected abstract String[] getCreateStatements();
}

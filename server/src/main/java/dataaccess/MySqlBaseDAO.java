package dataaccess;

import java.sql.*;

public abstract class MySqlBaseDAO {

	public MySqlBaseDAO() {
		try {
			configureDatabase();
		} catch (Exception e) {
			throw new RuntimeException("Failed to configure database", e);
		}
	}

	protected void executeUpdate(String statement, Object... params) throws DataAccessException {
		try (var conn = DatabaseManager.getConnection()) {
			try (var ps = conn.prepareStatement(statement)) {
				for (int i = 0; i < params.length; i++) {
					var param = params[i];
					if (param instanceof Integer p) {
						ps.setInt(i + 1, p);
					} else if (param instanceof String p) {
						ps.setString(i + 1, p);
					} else if (param == null) {
						ps.setNull(i + 1, Types.NULL);
					}
				}
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DataAccessException("Unable to update database: " + e.getMessage());
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

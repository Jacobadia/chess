package dataaccess;

import model.AuthData;

public class MySqlAuthDAO extends MySqlBaseDAO implements AuthDAO {

	@Override
	public void createAuth(AuthData auth) throws DataAccessException {
		if (authExists(auth.authToken())) {
			throw new DataAccessException("AuthToken Taken");
		}

		var statement = "INSERT INTO AuthTokens (authtoken, username) VALUES (?, ?)";
		executeUpdate(statement, auth.authToken(), auth.username());
	}

	@Override
	public AuthData getAuth(String aToken) throws DataAccessException {
		try (var conn = DatabaseManager.getConnection()) {
			var statement = "SELECT authtoken, username FROM AuthTokens WHERE authtoken=?";
			try (var ps = conn.prepareStatement(statement)) {
				ps.setString(1, aToken);
				try (var rs = ps.executeQuery()) {
					if (rs.next()) {
						return new AuthData(
								rs.getString("authtoken"),
								rs.getString("username"));
					}
				}
			}
		} catch (Exception e) {
			throw new DataAccessException("Auth Token Does Not Exist");
		}
		throw new DataAccessException("Auth Token Does Not Exist");
	}

	@Override
	public boolean authExists(String auth) {
		try {
			getAuth(auth);
			return true;
		} catch (DataAccessException e) {
			if (e.getMessage().equals("Auth Token Does Not Exist")) {
				return false;
			}
		}
		return false;
	}


	@Override
	public void clearAllAuths() throws DataAccessException {
		var statement = "DELETE FROM AuthTokens";
		executeUpdate(statement);
	}

	@Override
	public void deleteAuth(String authToken) throws DataAccessException {
		if (!authExists(authToken)) {
			throw new DataAccessException("Auth Token Does Not Exist");
		}
		var statement = "DELETE FROM AuthTokens WHERE authtoken=?";
		executeUpdate(statement, authToken);
	}

	@Override
	protected String[] getCreateStatements() {
		return new String[]{
            """
            CREATE TABLE IF NOT EXISTS AuthTokens (
              `authtoken` varchar(255) PRIMARY KEY,
              `username` varchar(100) NOT NULL,
              FOREIGN KEY (`username`) REFERENCES users(`username`) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
		};
	}
}

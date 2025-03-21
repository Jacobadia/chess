package dataaccess;

import model.UserData;

import org.mindrot.jbcrypt.BCrypt;


public class MySqlUserDAO extends MySqlBaseDAO implements UserDAO {

	@Override
	public void createUser(UserData user) throws DataAccessException {
		if (userExists(user.username())) {
			throw new DataAccessException("UserName Taken");
		}

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

		var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
		executeUpdate(statement, user.username(), hashedPassword, user.email());
	}

	@Override
	public UserData getUser(String username) throws DataAccessException {
		try (var conn = DatabaseManager.getConnection()) {
			var statement = "SELECT username, password, email FROM users WHERE username=?";
			try (var ps = conn.prepareStatement(statement)) {
				ps.setString(1, username);
				try (var rs = ps.executeQuery()) {
					if (rs.next()) {
						return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
								rs.getString("email"));
					}
				}
			}
		} catch (Exception e) {
			throw new DataAccessException("User Does Not Exist");
		}
		throw new DataAccessException("User Does Not Exist");
	}

    @Override
    public boolean verifyPassword(String username, String providedPassword) {
        try {
            UserData user = getUser(username);
            return BCrypt.checkpw(providedPassword, user.password());
        } catch (DataAccessException e) {
            return false;
        }
    }

	@Override
	public boolean userExists(String username) {
		try {
			getUser(username);
			return true;
		} catch (DataAccessException e) {
			if (e.getMessage().equals("User Does Not Exist")) {
				return false;
			}
		}
		return false;
	}


	@Override
	public void clearAllUsers() throws DataAccessException {
		var statement = "DELETE FROM users";
		executeUpdate(statement);
	}

	@Override
	protected String[] getCreateStatements() {
		return new String[]{
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(100) PRIMARY KEY,
              `password` varchar(100) NOT NULL,
              `email` varchar(100) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
		};
	}
}

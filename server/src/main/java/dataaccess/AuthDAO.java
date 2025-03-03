package dataaccess;

import model.AuthData;

public interface AuthDAO {
	void createAuth(AuthData auth) throws DataAccessException;

	AuthData getAuth(String authToken) throws DataAccessException;

	void updateAuth(AuthData auth) throws DataAccessException;

	void deleteAuth(String authToken) throws DataAccessException;

	void clearAllAuths() throws DataAccessException;
}

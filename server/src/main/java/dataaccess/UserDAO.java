package dataaccess;

import model.UserData;

public interface UserDAO {
	void createUser(UserData user) throws DataAccessException;

	UserData getUser(String username) throws DataAccessException;

	boolean userExists(String username) throws DataAccessException;

	void clearAllUsers();
}


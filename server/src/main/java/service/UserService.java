package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import java.util.UUID;

record RegisterRequest(String username, String password, String email) {
}

record RegisterResult(String authToken, String username) {
}

public class UserService {

	private final MemoryUserDAO userDAO = new MemoryUserDAO();
	private final MemoryAuthDAO authDAO = new MemoryAuthDAO();

	public RegisterResult register(RegisterRequest r) throws DataAccessException {
		// Verify input
		if (r.username() == null || r.password() == null || r.email() == null ||
				r.username().isBlank() || r.password().isBlank() || r.email().isBlank()) {
			throw new DataAccessException("Invalid registration input");
		}

		// Check if username is taken
		if (userDAO.userExists(r.username())) {
			throw new DataAccessException("Username is already taken");
		}

		// Create a new user model object
		UserData newUser = new UserData(r.username(), r.password(), r.email());

		// Insert new user into the database
		userDAO.createUser(newUser);

		// Log the user in & create a new authToken
		String authToken = UUID.randomUUID().toString();
		AuthData authData = new AuthData(authToken, r.username());
		authDAO.createAuth(authData);

		// Create a RegisterResult and return it
		return new RegisterResult(authToken, r.username());
	}

//	public LoginResult login(LoginRequest r) throws DataAccessException {
//	}
//
//	public LogoutResult logout(LogoutRequest r) throws DataAccessException {
//	}
}



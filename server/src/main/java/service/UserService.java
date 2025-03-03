package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import model.UserData;
import model.AuthData;

import java.util.UUID;

import service.requestresult.*;


public class UserService {

	private final MemoryUserDAO userDAO;
	private final MemoryAuthDAO authDAO;

	public UserService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
	}

	public RegisterResult register(RegisterRequest r) {

		try {
			if (r.username() == null || r.password() == null || r.email() == null ||
					r.username().isBlank() || r.password().isBlank() || r.email().isBlank()) {
				return new RegisterResult(null, null, "Error: bad request");
			}

			if (userDAO.userExists(r.username())) {
				return new RegisterResult(null, null, "Error: already taken");
			}

			UserData newUser = new UserData(r.username(), r.password(), r.email());
			userDAO.createUser(newUser);

			String authToken = generateToken();
			AuthData authData = new AuthData(authToken, r.username());
			authDAO.createAuth(authData);

			return new RegisterResult(authToken, r.username(), null);

		} catch (Exception e) {
			// Catch all unexpected exceptions and wrap in DataAccessException
			return new RegisterResult(null, null, "Error: " + e.getMessage());
		}
	}

//	public LoginResult login(LoginRequest r) throws DataAccessException {
//	}
//
//	public LogoutResult logout(LogoutRequest r) throws DataAccessException {
//	}

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}



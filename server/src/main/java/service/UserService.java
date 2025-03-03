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
			return new RegisterResult(null, null, "Error: " + e.getMessage());
		}
	}

	public RegisterResult login(RegisterRequest r) {

		try {
			if (!userDAO.userExists(r.username())) {
				return new RegisterResult(null, null, "Error: unauthorized");
			}

			if ( !userDAO.getUser(r.username()).password().equals(r.password()) ) {
				return new RegisterResult(null, null, "Error: unauthorized");
			}

			String authToken = generateToken();
			AuthData authData = new AuthData(authToken, r.username());
			authDAO.createAuth(authData);

			return new RegisterResult(authToken, r.username(), null);

		} catch (Exception e) {
			return new RegisterResult(null, null, "Error: " + e.getMessage());
		}
	}

	public RegisterResult logout(AuthTokenRequest r) {
		try {
			if (authDAO.authExists(r.authToken())) {
				authDAO.deleteAuth(r.authToken());
				return new RegisterResult(null, null, null);
			}
			else {
				return new RegisterResult(null, null, "Error: unauthorized");
			}
		} catch (Exception e) {
			return new RegisterResult(null, null, "Error: " + e.getMessage());
		}

	}

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}



package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import java.util.UUID;

import service.requestresult.*;


public class UserService {

	private final MemoryUserDAO userDAO = new MemoryUserDAO();
	private final MemoryAuthDAO authDAO = new MemoryAuthDAO();

	public RegisterResult register(RegisterRequest r) throws DataAccessException {

		if (r.username() == null || r.password() == null || r.email() == null ||
				r.username().isBlank() || r.password().isBlank() || r.email().isBlank()) {
			throw new DataAccessException("Invalid: Username, Password, and Email required");
		}

		if (userDAO.userExists(r.username())) {
			throw new DataAccessException("Username is already taken");
		}

		UserData newUser = new UserData(r.username(), r.password(), r.email());
		userDAO.createUser(newUser);

		String authToken = generateToken();
		AuthData authData = new AuthData(authToken, r.username());
		authDAO.createAuth(authData);

		return new RegisterResult(authToken, r.username());
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



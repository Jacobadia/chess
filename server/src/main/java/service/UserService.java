package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.UserData;
import model.AuthData;

import java.util.UUID;

import service.requestresult.*;


public class UserService {

	private final UserDAO userDAO;
	private final AuthDAO authDAO;

	public UserService(UserDAO userDAO, AuthDAO authDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
	}

	public AuthUserNameResult register(UserInfoRequest r) {

		try {
			if (r.username() == null || r.password() == null || r.email() == null ||
					r.username().isBlank() || r.password().isBlank() || r.email().isBlank()) {
				return new AuthUserNameResult(null, null, "Error: bad request");
			}

			if (userDAO.userExists(r.username())) {
				return new AuthUserNameResult(null, null, "Error: already taken");
			}

			UserData newUser = new UserData(r.username(), r.password(), r.email());
			userDAO.createUser(newUser);

			String authToken = generateToken();
			AuthData authData = new AuthData(authToken, r.username());
			authDAO.createAuth(authData);

			return new AuthUserNameResult(authToken, r.username(), null);

		} catch (Exception e) {
			return new AuthUserNameResult(null, null, "Error: " + e.getMessage());
		}
	}

	public AuthUserNameResult login(UserInfoRequest r) {

		try {
			if (!userDAO.userExists(r.username())) {
				return new AuthUserNameResult(null, null, "Error: unauthorized");
			}

			if (!userDAO.verifyPassword(r.username(), r.password())) {
				return new AuthUserNameResult(null, null, "Error: unauthorized");
			}

			String authToken = generateToken();
			AuthData authData = new AuthData(authToken, r.username());
			authDAO.createAuth(authData);

			return new AuthUserNameResult(authToken, r.username(), null);

		} catch (Exception e) {
			return new AuthUserNameResult(null, null, "Error: " + e.getMessage());
		}
	}

	public AuthUserNameResult logout(AuthTokenRequest r) {
		try {
			if (authDAO.authExists(r.authToken())) {
				authDAO.deleteAuth(r.authToken());
				return new AuthUserNameResult(null, null, null);
			} else {
				return new AuthUserNameResult(null, null, "Error: unauthorized");
			}
		} catch (Exception e) {
			return new AuthUserNameResult(null, null, "Error: " + e.getMessage());
		}

	}

	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}



package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

	final private Map<String, UserData> users = new HashMap<>();

	@Override
	public void createUser(UserData user) throws DataAccessException {

		if (users.containsKey(user.username())) {
			throw new DataAccessException("UserName Taken");
		}

		UserData myUser = new UserData(user.username(), user.password(), user.email());
		users.put(user.username(), myUser);
	}

	@Override
	public UserData getUser(String username) throws DataAccessException {
		if (!users.containsKey(username)) {
			throw new DataAccessException("User Does Not Exist");
		}
		return users.get(username);

	}

	@Override
	public boolean verifyPassword(String username, String providedPassword) {
		try {
			UserData user = getUser(username);
			return providedPassword.equals(user.password());
		} catch (DataAccessException e) {
			return false;
		}
	}

	@Override
	public boolean userExists(String username) {
		return users.containsKey(username);
	}

	@Override
	public void clearAllUsers() {
		users.clear();
	}
}


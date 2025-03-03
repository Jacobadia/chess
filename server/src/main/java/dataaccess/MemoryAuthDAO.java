package dataaccess;

import model.AuthData;

import java.util.Map;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

	final private Map<String, AuthData> auths = new HashMap<>();

	@Override
	public void createAuth(AuthData auth) throws DataAccessException {

		if (auths.containsKey(auth.authToken())) {
			throw new DataAccessException("AuthToken Taken");
		}

		AuthData myAuth = new AuthData(auth.authToken(), auth.username());
		auths.put(auth.authToken(), myAuth);
	}

	@Override
	public AuthData getAuth(String authToken) throws DataAccessException {
		if (!auths.containsKey(authToken)) {
			throw new DataAccessException("Auth Token Does Not Exist");
		}
		return auths.get(authToken);

	}

	@Override
	public void deleteAuth(String authToken) throws DataAccessException {
		if (!auths.containsKey(authToken)) {
			throw new DataAccessException("Auth Token Does Not Exist");
		}
		auths.remove(authToken);
	}

	@Override
	public boolean authExists(String auth) {
		return auths.containsKey(auth);
	}

	@Override
	public void clearAllAuths() {
		auths.clear();
	}
}


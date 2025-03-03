package dataaccess;

import model.UserData;
import java.util.Map;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final private Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {

        if(users.containsKey(user.username())){
            throw new DataAccessException("UserName Taken");
        }

        UserData myUser = new UserData(user.username(), user.password(), user.email());
        users.put(user.username(), myUser);
    }

    @Override
	public UserData getUser(String username) throws DataAccessException {
        if(!users.containsKey(username)){
            throw new DataAccessException("User Does Not Exist");
        }
        return users.get(username);

    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!users.containsKey(user.username())) {
            throw new DataAccessException("User Does Not Exist");
        }
        users.put(user.username(), new UserData(user.username(), user.password(), user.email()));
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("User Does Not Exist");
        }
        users.remove(username);
    }

    @Override
	public void clearAllUsers() {
        users.clear();
    }
}


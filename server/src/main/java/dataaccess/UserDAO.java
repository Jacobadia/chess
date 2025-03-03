package dataaccess;

import model.UserData;
import java.util.List;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}


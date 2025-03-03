package dataaccess;

import model.AuthData;
import java.util.List;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void updateAuth(AuthData auth) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    List<AuthData> getAllAuths() throws DataAccessException;
}

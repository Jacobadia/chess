package server;

import com.google.gson.Gson;
import exception.ResponseException;
import server.requestresult.*;


import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthUserNameResult register(String username, String password, String email) throws ResponseException {
        UserInfoRequest user = new UserInfoRequest(username, password, email);
        return this.makeRequest("POST", "/user", user, AuthUserNameResult.class);
    }

    public void deleteDatabase() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null);
    }

    public AuthUserNameResult  login(String username, String password) throws ResponseException {
        UserInfoRequest user = new UserInfoRequest(username, password, null);
        return this.makeRequest("POST", "/session", user, AuthUserNameResult .class);
    }

    public AuthUserNameResult logout(AuthTokenRequest auth) throws ResponseException {
        return this.makeRequest("DELETE", "/session", auth, AuthUserNameResult.class);
    }

    public ListGamesResult listGames(AuthTokenRequest authToken) throws ResponseException {
        return this.makeRequest("GET", "/game", null, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest game) throws ResponseException {
        return this.makeRequest("POST", "/game", game, CreateGameResult.class);
    }

    public MessageResult joinGame(JoinGameRequest game) throws ResponseException {
        return this.makeRequest("PUT", "/game", game, MessageResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

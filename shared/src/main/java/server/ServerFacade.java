package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import server.requestresult.*;


import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthUserNameResult register(String username, String password, String email) throws ResponseException {
        UserInfoRequest user = new UserInfoRequest(username, password, email);
        return this.makeRequest("POST", "/user", user, AuthUserNameResult.class, null);
    }

    public void deleteDatabase() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, null);
    }

    public AuthUserNameResult  login(String username, String password) throws ResponseException {
        UserInfoRequest user = new UserInfoRequest(username, password, null);
        return this.makeRequest("POST", "/session", user, AuthUserNameResult .class, null);
    }

    public AuthUserNameResult logout(String authTok) throws ResponseException {
        return this.makeRequest("DELETE", "/session", null, AuthUserNameResult.class, authTok);
    }

    public ArrayList listGames(String authTok) throws ResponseException {
        ListGamesResult r = this.makeRequest("GET", "/game", null, ListGamesResult.class, authTok);
        return r.games();
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ResponseException {
        CreateGameRequest gameRequest = new CreateGameRequest(gameName, authToken);
        return this.makeRequest("POST", "/game", gameRequest, CreateGameResult.class, authToken);
    }

    public MessageResult joinGame(ChessGame.TeamColor color, int id, String auth) throws ResponseException {
        JoinGameRequest gameRequest = new JoinGameRequest(color, id, auth);
        return this.makeRequest("PUT", "/game", gameRequest, MessageResult.class, auth);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

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

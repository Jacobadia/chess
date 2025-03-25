package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.deleteDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFailure() {
        assertThrows(ResponseException.class, () -> facade.register("", "", ""));
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertNotNull(authData);
        assertTrue(authData.length() > 10);
    }

    @Test
    void loginFailure() {
        assertThrows(ResponseException.class, () -> facade.login("invalidUser", "wrongPassword"));
    }

    @Test
    void logoutSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.logout(authData.authToken());
        assertNotNull(result);
        assertThrows(ResponseException.class, () -> facade.listGames(authData.authToken()));
    }

    @Test
    void logoutFailure() {
        assertThrows(ResponseException.class, () -> facade.logout("invalidToken"));
    }

    @Test
    void listGamesSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.listGames(authData.authToken());
        assertNotNull(result);
    }

    @Test
    void listGamesFailure() {
        assertThrows(ResponseException.class, () -> facade.listGames("invalidToken"));
    }

    @Test
    void createGameSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.createGame("Test Game", authData.authToken());
        assertNotNull(result);
    }

    @Test
    void createGameFailure() {
        assertThrows(ResponseException.class, () -> facade.createGame("Test Game", "invalidToken"));
    }

    @Test
    void joinGameSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        var id = facade.createGame("Test Game", authData.authToken());
        var result = facade.joinGame(ChessGame.TeamColor.WHITE, id, authData.authToken());
        assertNotNull(result);
    }

    @Test
    void joinGameFailure() {
        assertThrows(ResponseException.class,
                () -> facade.joinGame(ChessGame.TeamColor.WHITE, 999, "invalidToken"));
    }


}

package server.requestresult;

public record AuthUserNameResult(String authToken, String username, String message) {
}
package service.requestresult;

public record AuthUserNameResult(String authToken, String username, String message) {
}
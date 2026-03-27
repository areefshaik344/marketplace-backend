package com.markethub.ecommerce.exception;
import lombok.Getter;
@Getter
public class AppException extends RuntimeException {
    private final int status;
    public AppException(String message, int status) { super(message); this.status = status; }
    public static AppException notFound(String msg) { return new AppException(msg, 404); }
    public static AppException badRequest(String msg) { return new AppException(msg, 400); }
    public static AppException unauthorized(String msg) { return new AppException(msg, 401); }
    public static AppException forbidden(String msg) { return new AppException(msg, 403); }
    public static AppException conflict(String msg) { return new AppException(msg, 409); }
}

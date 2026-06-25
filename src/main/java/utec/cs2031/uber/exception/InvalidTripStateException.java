package utec.cs2031.uber.exception;

public class InvalidTripStateException extends RuntimeException {
    public InvalidTripStateException(String message) {
        super(message);
    }
}

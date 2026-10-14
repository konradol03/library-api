package pl.konradoldakowski.libraryapi.exception;

public class FineAlreadyExists extends RuntimeException {
    public FineAlreadyExists(String message) {
        super(message);
    }
}

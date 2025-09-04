package co.com.crediya.model.valueobject.exception;

public class InvalidDocumentIdException extends ValueObjectException{

    public InvalidDocumentIdException() {
        super("Document ID cannot be null or empty");
    }

    public InvalidDocumentIdException(String message) {
        super("Invalid documen id: " + message);
    }
    
}

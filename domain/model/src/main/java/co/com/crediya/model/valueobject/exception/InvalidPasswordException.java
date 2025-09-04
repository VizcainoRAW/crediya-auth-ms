package co.com.crediya.model.valueobject.exception;

public class InvalidPasswordException  extends ValueObjectException{

    public InvalidPasswordException(String reason) {
        super("Invalid Password, reason :" + reason);
    }

    public InvalidPasswordException(){
        super("Password cannot be null or empty");
    }
    
}

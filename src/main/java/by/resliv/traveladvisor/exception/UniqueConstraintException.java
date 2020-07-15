package by.resliv.traveladvisor.exception;

public class UniqueConstraintException extends RuntimeException {
    private static final long serialVersionUID = 6045851376912864728L;

    public UniqueConstraintException(String message) {
        super(message);
    }
}

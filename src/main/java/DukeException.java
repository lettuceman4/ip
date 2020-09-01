/**
 * <p>The DukeException class defines the behavior of an exception that is produced by Duke.</p>
 */
public class DukeException extends Exception {
    public DukeException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}

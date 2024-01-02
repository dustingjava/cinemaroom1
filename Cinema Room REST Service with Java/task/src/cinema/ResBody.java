package cinema;

public class ResBody {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    ResBody(String error) {
        this.error = error;
    }
}

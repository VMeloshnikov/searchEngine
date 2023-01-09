package searchengine.response;

public class FalseResponseServiсe implements ResponseService{

    private final String error;

    public FalseResponseServiсe(String error) {
        this.error = error;
    }

    @Override
    public boolean getResult () {
        return false;
    }

    public String getError() {
        return error;
    }
}

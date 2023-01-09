package searchengine.response;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.DAO.SearchDataDAO;

@Getter
@Setter
public class SearchResponseService implements ResponseService {

    private boolean result;
    private int count;
    private SearchDataDAO[] data;

    public SearchResponseService(boolean result) {
        this.result = result;
    }

    public SearchResponseService(boolean result, int count, SearchDataDAO[] data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }

    @Override
    public boolean getResult() {
        return result;
    }

}

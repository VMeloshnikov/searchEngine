package searchengine.response.indexResponseEntity;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.DAO.StatusDAO;
@Getter
@Setter
public class Detailed {
    String url;
    String name;
    StatusDAO statusDAO;
    long statusTime;
    String error;
    long pages;
    long lemmas;

    public Detailed(String url, String name, StatusDAO statusDAO,
                    long statusTime, String error, long pages, long lemmas) {
        this.url = url;
        this.name = name;
        this.statusDAO = statusDAO;
        this.statusTime = statusTime;
        this.error = error;
        this.pages = pages;
        this.lemmas = lemmas;
    }
}

package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDataDAO {
    String site;
    String siteName;
    String uri;
    String title;
    String snippet;
    double relevance;

    public SearchDataDAO() {

    }

    public SearchDataDAO(String site, String siteName, String uri, String title, String snippet, double relevance) {
        this.site = site;
        this.siteName = siteName;
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }

    @Override
    public String toString() {
        return "SearchDDataDAO{" +
                "uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                ", relevance=" + relevance +
                '}';
    }
}

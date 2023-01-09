package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDAO {
    private String url;
    private String title;
    private String snippet;
    private double relevance;
}

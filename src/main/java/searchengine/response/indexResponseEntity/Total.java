package searchengine.response.indexResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Total {
    long sites;
    long pages;
    long lemmas;
    boolean isIndexing;

    public Total(long sites, long pages, long lemmas, boolean isIndexing) {
        this.sites = sites;
        this.pages = pages;
        this.lemmas = lemmas;
        this.isIndexing = isIndexing;
    }
}

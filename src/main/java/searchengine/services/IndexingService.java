package searchengine.services;

import searchengine.response.ResponseService;

public interface IndexingService {
    ResponseService startIndexingAll();

    ResponseService stopIndexing();

    ResponseService startIndexingOne(String url);


}

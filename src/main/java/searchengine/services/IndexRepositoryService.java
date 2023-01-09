package searchengine.services;

import searchengine.Index;
import searchengine.model.DAO.IndexingDAO;

import java.util.List;

public interface IndexRepositoryService {
    List<IndexingDAO> getAllIndexingByLemmaId(int lemmaId);

    List<IndexingDAO> getAllIndexingByPageId(int pageId);

    void deleteAllIndexing(List<IndexingDAO> indexingDAOList);

    IndexingDAO getIndexing(int lemmaId, int pageId);

    void save(IndexingDAO indexingDAO);
}

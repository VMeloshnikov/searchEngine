package searchengine.services;

import searchengine.model.DAO.IndexingDAO;
import searchengine.model.DAO.LemmaDAO;

import java.util.List;

public interface LemmaRepositoryService {
    List<LemmaDAO> getLemma(String lemmaName);

    void save(LemmaDAO lemmaDAO);

    long lemmaCount();

    long lemmaCount(long siteId);

    void deleteAllLemmas(List<LemmaDAO> lemmaDAOList);

    List<LemmaDAO> findLemmaByIndexing(List<IndexingDAO> indexingDAOList);
}

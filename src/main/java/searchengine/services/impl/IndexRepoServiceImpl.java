package searchengine.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.repository.IndexRepository;
import searchengine.model.DAO.IndexingDAO;
import searchengine.services.IndexRepositoryService;

import java.util.List;

@Service
public class IndexRepoServiceImpl implements IndexRepositoryService {

    private static final Log log = LogFactory.getLog(IndexRepoServiceImpl.class);

    private final IndexRepository indexRepository;

    public IndexRepoServiceImpl(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Override
    public List<IndexingDAO> getAllIndexingByLemmaId(int lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }

    @Override
    public List<IndexingDAO> getAllIndexingByPageId(int pageId) {
        return indexRepository.findByPageId(pageId);
    }

    @Override
    public void deleteAllIndexing(List<IndexingDAO> indexingDAOList) {
        indexRepository.deleteAll(indexingDAOList);
    }

    @Override
    public IndexingDAO getIndexing(int lemmaId, int pageId) {
        IndexingDAO indexingDAO = null;
        try {
            indexingDAO = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
        } catch (Exception e) {
            log.error("lemmaId: " + lemmaId + " + pageId" + pageId + " not found");
            log.error(e.getMessage());
        }
        return indexingDAO;
    }

    @Override
    public synchronized void save(IndexingDAO indexingDAO) {
        indexRepository.save(indexingDAO);
    }
}

package searchengine.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.repository.LemmaRepository;
import searchengine.model.DAO.IndexingDAO;
import searchengine.model.DAO.LemmaDAO;
import searchengine.services.LemmaRepositoryService;

import java.util.List;

@Service
public class LemmaRepoServiceImpl implements LemmaRepositoryService {

    private static final Log log = LogFactory.getLog(LemmaRepoServiceImpl.class);

    private final LemmaRepository lemmaRepository;

    public LemmaRepoServiceImpl(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }


    @Override
    public List<LemmaDAO> getLemma(String lemmaName) {
        List<LemmaDAO> lemmaDAOS = null;
        try {
            lemmaDAOS = lemmaRepository.findByLemma(lemmaName);
        } catch (Exception e) {
            log.error(lemmaName + "-" + e.getMessage());
        }
        return lemmaDAOS;
    }

    @Override
    public synchronized void save(LemmaDAO lemmaDAO) {
        lemmaRepository.save(lemmaDAO);
    }

    @Override
    public long lemmaCount() {
        return lemmaRepository.count();
    }

    @Override
    public long lemmaCount(long siteId) {
        return lemmaRepository.count(siteId);
    }

    @Override
    public void deleteAllLemmas(List<LemmaDAO> lemmaDAOList) {
        lemmaRepository.deleteAll(lemmaDAOList);
    }

    @Override
    public List<LemmaDAO> findLemmaByIndexing(List<IndexingDAO> indexingDAOList) {
        int[] lemmaIdList = new int[indexingDAOList.size()];
        for (int i = 0; i < indexingDAOList.size(); i++) {
            lemmaIdList[i] = indexingDAOList.get(i).getLemmaId();
        }
        return lemmaRepository.findById(lemmaIdList);
    }
}

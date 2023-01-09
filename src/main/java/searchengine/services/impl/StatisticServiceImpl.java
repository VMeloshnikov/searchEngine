package searchengine.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.response.StatisticResponseService;
import searchengine.response.indexResponseEntity.Detailed;
import searchengine.response.indexResponseEntity.Statistics;
import searchengine.response.indexResponseEntity.Total;
import searchengine.model.DAO.SiteDAO;
import searchengine.model.DAO.StatusDAO;
import searchengine.services.LemmaRepositoryService;
import searchengine.services.PageRepositoryService;
import searchengine.services.SiteRepositoryService;
import searchengine.services.StatisticService;

import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {

    private static final Log log = LogFactory.getLog(StatisticServiceImpl.class);

    private final SiteRepositoryService siteRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final PageRepositoryService pageRepositoryService;

    public StatisticServiceImpl(SiteRepositoryService siteRepositoryService,
                                LemmaRepositoryService lemmaRepositoryService,
                                PageRepositoryService pageRepositoryService) {
        this.siteRepositoryService = siteRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
    }

    private Total getTotal() {
        long sites = siteRepositoryService.siteCount();
        long lemmas = lemmaRepositoryService.lemmaCount();
        long pages = pageRepositoryService.pageCount();
        boolean isIndexing = isSitesIndexing();
        return new Total(sites, pages, lemmas, isIndexing);
    }

    private Detailed getDetailed(SiteDAO siteDAO) {
        String url = siteDAO.getUrl();
        String name = siteDAO.getName();
        StatusDAO statusDAO = siteDAO.getStatus();
        long statusTime = siteDAO.getStatusTime().getTime();
        String error = siteDAO.getLastError();
        long pages = pageRepositoryService.pageCount(siteDAO.getId());
        long lemmas = lemmaRepositoryService.lemmaCount(siteDAO.getId());
        return new Detailed(url, name, statusDAO, statusTime, error, pages, lemmas);
    }

    private boolean isSitesIndexing() {
        boolean is = true;
        for (SiteDAO s : siteRepositoryService.getAllSites()) {
            if (s.getStatus() == null || !s.getStatus().equals(StatusDAO.INDEXING)) {
                is = false;
                break;
            }
        }
        return is;
    }
    @Override
    public StatisticResponseService getStatistic() {
        Total total = getTotal();
        List<SiteDAO> siteDAOList = siteRepositoryService.getAllSites();
        Detailed[] detaileds = new Detailed[siteDAOList.size()];
        for (int i = 0; i < siteDAOList.size(); i++) {
            detaileds[i] = getDetailed(siteDAOList.get(i));
        }
        log.info("Получение статистики");
        return new StatisticResponseService(true, new Statistics(total, detaileds));
    }
}

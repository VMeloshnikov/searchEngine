package searchengine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import searchengine.config.SearchSettings;
import searchengine.config.SitesList;
import searchengine.model.DAO.FieldDAO;
import searchengine.model.DAO.SiteDAO;
import searchengine.model.DAO.StatusDAO;
import searchengine.services.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Index {

    private static final Log LOG = LogFactory.getLog(Index.class);
    private final SearchSettings searchSettings;

    private final SitesList sitesList;
    private final FieldRepositoryService fieldRepositoryService;
    private final SiteRepositoryService siteRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;

    private ExecutorService executor;

    public Index(SearchSettings searchSettings,
                 SitesList sitesList, FieldRepositoryService fieldRepositoryService,
                 SiteRepositoryService siteRepositoryService,
                 IndexRepositoryService indexRepositoryService,
                 PageRepositoryService pageRepositoryService,
                 LemmaRepositoryService lemmaRepositoryService) {
        this.searchSettings = searchSettings;
        this.sitesList = sitesList;
        this.fieldRepositoryService = fieldRepositoryService;
        this.siteRepositoryService = siteRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
    }



    public boolean allSiteIndexing() {

        fieldInit();
        boolean isIndexing;
        List<SiteDAO> siteDAOList = getSiteListFromConfig();
        executor = Executors.newFixedThreadPool(12);
        for (SiteDAO siteDAO : siteDAOList) {
            isIndexing = startSiteIndexing(siteDAO);
            if (!isIndexing) {
                stopSiteIndexing();
                return false;
            }
        }
        return true;
    }

    public String checkedSiteIndexing(String url) {
        List<SiteDAO> siteDAOList = siteRepositoryService.getAllSites();
        String baseUrl = "";
        for (SiteDAO siteDAO : siteDAOList) {
            if (siteDAO.getStatus() != StatusDAO.INDEXING) {
                return "false";
            }
            if (url.contains(siteDAO.getUrl())) {
                baseUrl = siteDAO.getUrl();
            }
        }
        if (baseUrl.isEmpty()) {
            return "not found";
        } else {
            SiteDAO siteDAO = siteRepositoryService.getSite(baseUrl);
            siteDAO.setUrl(url);
            SiteIndexing indexing = new SiteIndexing(
                    siteDAO,
                    searchSettings,
                    fieldRepositoryService,
                    siteRepositoryService,
                    indexRepositoryService,
                    pageRepositoryService,
                    lemmaRepositoryService,
                    false);
            executor.execute(indexing);
            siteDAO.setUrl(baseUrl);
            siteRepositoryService.save(siteDAO);
            return "true";
        }
    }

    private void fieldInit() {
        FieldDAO fieldDAOTitle = new FieldDAO("title", "title", 1.0f);
        FieldDAO fieldDAOBody = new FieldDAO("body", "body", 0.8f);
        if (fieldRepositoryService.getFieldByName("title") == null) {
            fieldRepositoryService.save(fieldDAOTitle);
            fieldRepositoryService.save(fieldDAOBody);
        }
    }

    private boolean startSiteIndexing(SiteDAO siteDAO) {

        SiteDAO siteDAO1;
        siteDAO1 = siteRepositoryService.getSite(siteDAO.getUrl());
        if (siteDAO1.getUrl() == null) {
            siteRepositoryService.save(siteDAO);
            SiteIndexing indexing = new SiteIndexing(
                    siteRepositoryService.getSite(siteDAO.getUrl()),
                    searchSettings,
                    fieldRepositoryService,
                    siteRepositoryService,
                    indexRepositoryService,
                    pageRepositoryService,
                    lemmaRepositoryService,
                    true);
            executor.execute(indexing);
            return true;
        } else {
            if (!siteDAO1.getStatus().equals(StatusDAO.INDEXING)) {
                SiteIndexing indexing = new SiteIndexing(
                        siteRepositoryService.getSite(siteDAO.getUrl()),
                        searchSettings,
                        fieldRepositoryService,
                        siteRepositoryService,
                        indexRepositoryService,
                        pageRepositoryService,
                        lemmaRepositoryService,
                        true);
                executor.execute(indexing);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean stopSiteIndexing() {
        boolean isThreadAlive;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(2, TimeUnit.SECONDS))
                    LOG.error("Pool did not terminate");
            }
            isThreadAlive = true;
        } catch (InterruptedException ex) {
            LOG.error("Ошибка закрытия потоков: " + ex);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            isThreadAlive = true;
        }

        List<SiteDAO> siteDAOList = siteRepositoryService.getAllSites();
        for (SiteDAO siteDAO : siteDAOList) {
            siteDAO.setStatusDAO(StatusDAO.FAILED);
            siteDAO.setLastError("Индексация остановлена пользователем");
            siteRepositoryService.save(siteDAO);
        }

        return isThreadAlive;
    }

    private List<SiteDAO> getSiteListFromConfig() {
        List<SiteDAO> siteDAOList = new ArrayList<>();
        List<HashMap<String, String>> sites = sitesList.getSites();
        for (HashMap<String, String> map : sites) {
            String url = "";
            String name = "";
            for (Map.Entry<String, String> siteInfo : map.entrySet()) {
                if (siteInfo.getKey().equals("name")) {
                    name = siteInfo.getValue();
                }
                if (siteInfo.getKey().equals("url")) {
                    url = siteInfo.getValue();
                }
            }
            SiteDAO siteDAO = new SiteDAO();
            siteDAO.setUrl(url);
            siteDAO.setName(name);
            siteDAO.setStatusTime(new Date());
            siteDAOList.add(siteDAO);
        }
        return siteDAOList;
    }
}
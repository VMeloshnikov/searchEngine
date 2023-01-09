package searchengine.services;

import searchengine.model.DAO.SiteDAO;

import java.util.List;

public interface SiteRepositoryService {
    SiteDAO getSite(String url);

    SiteDAO getSite(int siteId);

    void save(SiteDAO siteDAO);

    long siteCount();

    List<SiteDAO> getAllSites();

}

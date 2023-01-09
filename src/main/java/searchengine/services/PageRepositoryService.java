package searchengine.services;

import searchengine.model.DAO.PageDAO;

import java.util.Optional;

public interface PageRepositoryService {
    PageDAO getPage(String pagePath);

    void save(PageDAO pageDAO);

    Optional<PageDAO> findPageById(int id);

    Optional<PageDAO> findPageByPageIdAndSiteId(int pageId, int siteId);

    long pageCount();

    long pageCount(long siteId);

    void deletePage(PageDAO pageDAO);

}

package searchengine.services.impl;

import org.springframework.stereotype.Service;
import searchengine.model.DAO.PageDAO;
import searchengine.repository.PageRepository;
import searchengine.services.PageRepositoryService;

import java.util.Optional;

@Service
public class PageRepoServiceImpl implements PageRepositoryService {

    private final PageRepository pageRepository;

    public PageRepoServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public PageDAO getPage(String pagePath) {

        return pageRepository.findByPath(pagePath);
    }

    @Override
    public synchronized void save(PageDAO pageDAO) {
        if (pageDAO.getContent() != null || pageDAO.getPath() != null) {
            if (pageRepository.findByPath(pageDAO.getPath()) == null) {
                pageRepository.save(pageDAO);
            }

        }
    }

    @Override
    public Optional<PageDAO> findPageById(int id) {
        return pageRepository.findById(id);
    }

    @Override
    public Optional<PageDAO> findPageByPageIdAndSiteId(int pageId, int siteId) {
        return pageRepository.findByIdAndSiteId(pageId, siteId);
    }

    @Override
    public long pageCount() {
        return pageRepository.count();
    }

    @Override
    public long pageCount(long siteId) {
        return pageRepository.count(siteId);
    }

    @Override
    public void deletePage(PageDAO pageDAO) {
        pageRepository.delete(pageDAO);
    }
}

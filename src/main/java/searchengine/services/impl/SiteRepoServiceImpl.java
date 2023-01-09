package searchengine.services.impl;

import org.springframework.stereotype.Service;
import searchengine.model.DAO.SiteDAO;
import searchengine.repository.SiteRepository;
import searchengine.services.SiteRepositoryService;

import java.util.List;
import java.util.Optional;

@Service
public class SiteRepoServiceImpl implements SiteRepositoryService {

    private final SiteRepository siteRepository;

    public SiteRepoServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public SiteDAO getSite(String url) {
        return siteRepository.findByUrl(url).orElseGet(SiteDAO::new);
    }

    @Override
    public SiteDAO getSite(int siteId) {
        return siteRepository.findById(siteId).orElseGet(SiteDAO::new);
    }

    @Override
    public void save(SiteDAO siteDAO) {
        Optional<SiteDAO> siteDAOOptional = siteRepository.findByUrl(siteDAO.getUrl());
        if (siteDAOOptional.isPresent()) {
            SiteDAO dbSite = siteDAOOptional.get();
            if (dbSite.getUrl().equalsIgnoreCase(siteDAO.getUrl())) {
                dbSite.setStatusDAO(siteDAO.getStatusDAO());
                dbSite.setStatusTime(siteDAO.getStatusTime());
                dbSite.setLastError(siteDAO.getLastError());
                siteRepository.save(dbSite);
            }
        } else {
            siteRepository.save(siteDAO);
        }
    }

    @Override
    public long siteCount() {
        return siteRepository.count();
    }

    @Override
    public List<SiteDAO> getAllSites() {
        return siteRepository.findAll();
    }
}

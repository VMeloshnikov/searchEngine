package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.DAO.SiteDAO;

import java.util.Optional;

public interface SiteRepository extends JpaRepository <SiteDAO, Integer> {

    Optional<SiteDAO> findByUrl(String url);

//    SiteDAO findByUrl (String url);
}

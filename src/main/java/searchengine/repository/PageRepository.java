package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.DAO.PageDAO;

import java.util.Optional;
@Repository
public interface PageRepository extends JpaRepository<PageDAO, Integer> {
    PageDAO findByPath (String path);

    Optional<PageDAO> findByIdAndSiteId (int id, int siteId);

    @Query(value = "SELECT count(*) from PageDAO where site_id = :id")
    long count(@Param("id") long id);

//    @Query(value = "SELECT PageDAO FROM PageDAO where site_id = :siteId and content = :content")
//    Optional<PageDAO> findBySiteIdAndContent(@Param("content") String content, @Param("siteId") int siteId);
}

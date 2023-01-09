package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import searchengine.model.DAO.LemmaDAO;

import java.util.List;

public interface LemmaRepository extends JpaRepository<LemmaDAO, Integer> {
    List<LemmaDAO> findByLemma (String Lemma);

    @Query(value = "SELECT * from search_lemma WHERE id IN(:id)", nativeQuery = true)
    List<LemmaDAO> findById(int[] id);

    @Query(value = "SELECT count(*) from LemmaDAO where site_id = :id")
    long count(@Param("id") long id);
}

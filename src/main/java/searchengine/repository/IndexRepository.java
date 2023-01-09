package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.DAO.IndexingDAO;

import java.util.List;
@Repository
public interface IndexRepository extends JpaRepository<IndexingDAO, Integer> {
    List<IndexingDAO> findByLemmaId (int lemmaId);
    List<IndexingDAO> findByPageId (int pageId);
    IndexingDAO findByLemmaIdAndPageId (int lemmaId, int pageId);
}

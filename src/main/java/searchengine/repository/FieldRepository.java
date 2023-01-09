package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.DAO.FieldDAO;
@Repository
public interface FieldRepository  extends JpaRepository<FieldDAO, Integer> {
    FieldDAO findByName(String name);
}

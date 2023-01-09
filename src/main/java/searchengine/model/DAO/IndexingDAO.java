package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Search_index")
public class IndexingDAO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "page_id")
    private int pageId;
    @Column(name = "lemma_id")
    private int lemmaId;
    private float ranking;

    public IndexingDAO() {
    }

    public IndexingDAO(int pageId, int lemmaId, float ranking) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.ranking = ranking;
    }
}

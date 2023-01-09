package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "search_lemma",
        indexes = {@Index(name = "lemma_INDX", columnList = "lemma")})

public class LemmaDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String lemma;
    private int frequency;
    @Column(name = "site_id")
    private int siteId;

    public LemmaDAO() {
    }

    public LemmaDAO(String lemma, int frequency, int siteId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.siteId = siteId;
    }
    @Override
    public String toString() {
        return "Lemma{" +
                "id" + id +
                ", lemma=" + lemma + '\'' +
                ", frequency" + frequency +
                ", siteId=" + siteId +
                '}';
    }
}

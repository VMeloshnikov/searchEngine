package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
@Getter
@Setter
@ToString
@Entity
@Table(name="Search_field",
        indexes = {@Index(name = "selector_INDX", columnList = "selector")})
public class FieldDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String selector;
    private float weight;

    public FieldDAO() {
    }
    public FieldDAO(String name, String selector, float weight) {
        this.name = name;
        this.selector = selector;
        this.weight = weight;
    }

}

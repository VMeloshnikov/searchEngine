package searchengine.model.DAO;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Search_page",
        indexes = {@Index(name = "Path_INDX", columnList = "path")})
public class PageDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String path;
    private int code;
    @Type(type = "text")
    private String content;
    @Column(name = "Site_id")
    private int siteId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

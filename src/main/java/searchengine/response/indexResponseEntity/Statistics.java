package searchengine.response.indexResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Statistics {
    Total total;
    Detailed[] detailed;

    public Statistics(Total total, Detailed[] detailed) {
        this.total = total;
        this.detailed = detailed;
    }
}

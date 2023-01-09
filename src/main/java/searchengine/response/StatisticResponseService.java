package searchengine.response;

import lombok.Getter;
import lombok.Setter;
import searchengine.response.indexResponseEntity.Statistics;

@Getter
@Setter

public class StatisticResponseService implements ResponseService{

    boolean result;
    Statistics statistics;

    public StatisticResponseService(boolean result, Statistics statistics) {
        this.result = result;
        this.statistics = statistics;
    }


    @Override
    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}

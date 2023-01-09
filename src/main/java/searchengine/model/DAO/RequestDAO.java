package searchengine.model.DAO;

import searchengine.services.morphological.MorphologicalAnalysis;

import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    private String req;
    private List<String> reqLemmas;

    public List<String> getReqLemmas() {
        return reqLemmas;
    }
    public String getReq() {
        return req;
    }
    public RequestDAO(String req) {
        this.req = req;
        reqLemmas = new ArrayList<>();
        try {
            MorphologicalAnalysis analyzer = new MorphologicalAnalysis();
            reqLemmas.addAll(analyzer.getLemmas(req));
        } catch (Exception e) {
            System.out.println("Ошибка морфологического анализа");
        }
    }
}

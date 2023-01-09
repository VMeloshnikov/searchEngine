package searchengine.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.Search;
import searchengine.model.DAO.RequestDAO;
import searchengine.response.FalseResponseServiсe;
import searchengine.response.ResponseService;
import searchengine.services.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Log log = LogFactory.getLog(SearchServiceImpl.class);

    private final Search search;

    public SearchServiceImpl(Search search) {
        this.search = search;
    }

    ResponseService response;

    @Override
    public ResponseService getResponse(RequestDAO requestDAO, String url, int offset, int limit) {
        log.info("Запрос на поиск строки- \"" + requestDAO.getReq() + "\"");
        if (requestDAO.getReq().equals("")) {
            response = new FalseResponseServiсe("Задан пустой поисковый запрос");
            log.warn("Задан пустой поисковый запрос");
            return response;
        }
        if (url.equals("")) {
            response = search.searchService(requestDAO, null, offset, limit);
        } else {
            response = search.searchService(requestDAO, url, offset, limit);
        }
        if (response.getResult()) {
            log.info("Запрос на поиск строки обработан, результат получен.");
            return response;
        } else {
            log.warn("Запрос на поиск строки обработан, указанная страница не найдена.");
            return new FalseResponseServiсe("Указанная страница не найдена");
        }
    }
}

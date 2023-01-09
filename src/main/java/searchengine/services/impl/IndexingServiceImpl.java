package searchengine.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.Index;
import searchengine.response.FalseResponseServiсe;
import searchengine.response.ResponseService;
import searchengine.response.TrueResponseService;
import searchengine.services.IndexingService;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final Index index;
    private static final Log log = LogFactory.getLog(IndexingServiceImpl.class);
    private static final String INDEXING_ERROR = "Ошибка запуска индексации";

    public IndexingServiceImpl(Index index) {
        this.index = index;
    }

    @Override
    public ResponseService startIndexingAll() {
        ResponseService response;
        boolean indexing;
        indexing = index.allSiteIndexing();
        log.info("Попытка запуска индексации всех сайтов");
        if (indexing) {
            response = new TrueResponseService();
            log.info("Индексация всех сайтов запущена");
        } else {
            response = new FalseResponseServiсe("Индексация всех сайтов уже запущена");
            log.warn("Индексация всех сайтов не запущена, т.к. процесс индексации был запущен ранее.");
        }
        return response;
    }

    @Override
    public ResponseService stopIndexing() {
        boolean indexing = index.stopSiteIndexing();
        log.info("Попытка остановки индексации");
        ResponseService response;
        if (indexing) {
            response = new TrueResponseService();
            log.info("Индексация остановлена");
        } else {
            response = new FalseResponseServiсe("Индексация не запущена");
            log.warn("Остановка индексации не может быть выполнена, потому что процесс индексации не запущен.");
        }
        return response;
    }

    @Override
    public ResponseService startIndexingOne(String url) {
        ResponseService resp;
        String response;
        response = index.checkedSiteIndexing(url);

        if (response.equals("not found")) {
            resp = new FalseResponseServiсe("Страница отсутствует в списке конфигурации");
        } else if (response.equals("false")) {
            resp = new FalseResponseServiсe("Индексация одного сайта уже запущена");
        } else {
            resp = new TrueResponseService();
        }
        return resp;
    }
}

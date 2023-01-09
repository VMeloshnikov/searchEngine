package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import searchengine.model.DAO.RequestDAO;
import searchengine.response.ResponseService;
import searchengine.services.impl.SearchServiceImpl;

@Controller
@RequestMapping("/api")
public class SearchController {

    private final SearchServiceImpl search;

    public SearchController(SearchServiceImpl search) {
        this.search = search;
    }


    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "site", required = false, defaultValue = "") String site,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "0") int limit) {
        ResponseService service = search.getResponse(new RequestDAO(query), site, offset, limit);
        return ResponseEntity.ok(service);
    }
}

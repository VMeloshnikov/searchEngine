package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.response.ResponseService;
import searchengine.model.DAO.RequestDAO;

import java.io.IOException;

@Service
public interface SearchService {

    ResponseService getResponse(RequestDAO requestDAO, String url, int offset, int limit);
}

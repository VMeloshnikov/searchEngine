package searchengine.services;

import searchengine.model.DAO.FieldDAO;

import java.util.List;

public interface FieldRepositoryService {
    FieldDAO getFieldByName(String fieldName);

    void save(FieldDAO fieldDAO);

    List<FieldDAO> getAllField();
}

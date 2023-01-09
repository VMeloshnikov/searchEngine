package searchengine.services.impl;

import org.springframework.stereotype.Service;
import searchengine.repository.FieldRepository;
import searchengine.model.DAO.FieldDAO;
import searchengine.services.FieldRepositoryService;

import java.util.ArrayList;
import java.util.List;

@Service
public class FieldRepoServiceImpl implements FieldRepositoryService {

    private final FieldRepository fieldRepository;

    public FieldRepoServiceImpl(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public FieldDAO getFieldByName(String fieldName) {
        return fieldRepository.findByName(fieldName);
    }

    @Override
    public synchronized void save(FieldDAO fieldDAO) {
        fieldRepository.save(fieldDAO);
    }

    @Override
    public List<FieldDAO> getAllField() {
        List<FieldDAO> list = new ArrayList<>();
        Iterable<FieldDAO> iterable = fieldRepository.findAll();
        iterable.forEach(list::add);
        return list;
    }
}

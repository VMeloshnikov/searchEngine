package searchengine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import searchengine.config.SearchSettings;
import searchengine.model.DAO.*;
import searchengine.services.*;
import searchengine.services.morphological.MorphologicalAnalysis;
import searchengine.services.sitemap.SiteMapBuilder;

import java.io.IOException;
import java.util.*;

public class SiteIndexing extends Thread{
    private final SiteDAO siteDAO;
    private final SearchSettings searchSettings;
    private final FieldRepositoryService fieldRepositoryService;
    private final SiteRepositoryService siteRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final boolean allSites;


    public SiteIndexing(SiteDAO siteDAO,
                        SearchSettings searchSettings,
                        FieldRepositoryService fieldRepositoryService,
                        SiteRepositoryService siteRepositoryService,
                        IndexRepositoryService indexRepositoryService,
                        PageRepositoryService pageRepositoryService,
                        LemmaRepositoryService lemmaRepositoryService,
                        boolean allSites) {
        this.siteDAO = siteDAO;
        this.searchSettings = searchSettings;
        this.fieldRepositoryService = fieldRepositoryService;
        this.siteRepositoryService = siteRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
        this.allSites = allSites;
    }

    @Override
    public void run() {
        try {
            if (allSites) {
                runAllIndexing();
            } else {
                runOneSiteIndexing(siteDAO.getUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllIndexing() {
        siteDAO.setStatusDAO(StatusDAO.INDEXING);
        siteDAO.setStatusTime(new Date());
        siteRepositoryService.save(siteDAO);
        SiteMapBuilder builder = new SiteMapBuilder(siteDAO.getUrl(), this.isInterrupted());
        builder.builtSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        for (String url : allSiteUrls) {
            runOneSiteIndexing(url);
        }
    }

    public void runOneSiteIndexing(String searchUrl) {
        siteDAO.setStatus(StatusDAO.INDEXING);
        siteDAO.setStatusTime(new Date());
        siteRepositoryService.save(siteDAO);
        List<FieldDAO> fieldDAOList = getFieldListFromDB();
        try {
            PageDAO pageDAO = getSearchPage(searchUrl, siteDAO.getUrl(), siteDAO.getId());
            PageDAO checkPageDAO = pageRepositoryService.getPage(searchUrl.replaceAll(siteDAO.getUrl(), ""));
            if (checkPageDAO != null) {
                prepareDbToIndexing(checkPageDAO);
            }
            TreeMap<String, Integer> map = new TreeMap<>();
            TreeMap<String, Float> indexing = new TreeMap<>();
            for (FieldDAO fieldDAO : fieldDAOList) {
                String name = fieldDAO.getName();
                float weight = fieldDAO.getWeight();
                String stringByTeg = getStringByTeg(name, pageDAO.getContent());
                MorphologicalAnalysis analysis = new MorphologicalAnalysis();
                TreeMap<String, Integer> tempMap = analysis.textAnalysis(stringByTeg);
                map.putAll(tempMap);
                indexing.putAll(indexingLemmas(tempMap, weight));
            }
            lemmaToDB(map, siteDAO.getId());
            map.clear();
            pageToDB(pageDAO);
            indexingToDB(indexing, pageDAO.getPath());
            indexing.clear();
        } catch (UnsupportedMimeTypeException e) {
            siteDAO.setLastError("Формат страницы не поддерживается: " + searchUrl);
            siteDAO.setStatus(StatusDAO.FAILED);
            siteRepositoryService.save(siteDAO);
        } catch (IOException e) {
            siteDAO.setLastError("Ошибка чтения страницы: " + searchUrl + "\n" + e.getMessage());
            siteDAO.setStatus(StatusDAO.FAILED);
            siteRepositoryService.save(siteDAO);
        }
    }

    private void pageToDB(PageDAO pageDAO) {
        pageRepositoryService.save(pageDAO);
        siteDAO.setStatus(StatusDAO.INDEXED);
        siteDAO.setStatusTime(new Date());
        siteRepositoryService.save(siteDAO);
    }

    private PageDAO getSearchPage(String url, String baseUrl, int siteId) throws IOException {
        PageDAO pageDAO = new PageDAO();
        Connection.Response response = Jsoup.connect(url)
                .userAgent(searchSettings.getAgent())
                .referrer("http://google.com")
                .execute();

        String content = response.body();
        String path = url.replaceAll(baseUrl, "");
        int code = response.statusCode();
        pageDAO.setCode(code);
        pageDAO.setPath(path);
        pageDAO.setContent(content);
        pageDAO.setSiteId(siteId);
        return pageDAO;
    }

    private List<FieldDAO> getFieldListFromDB() {
        List<FieldDAO> list = new ArrayList<>();
        Iterable<FieldDAO> iterable = fieldRepositoryService.getAllField();
        iterable.forEach(list::add);
        return list;
    }

    private String getStringByTeg(String teg, String html) {
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select(teg);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (!(builder.length() < 1)) {
            string = builder.toString();
        }
        return string;
    }

    private void lemmaToDB(TreeMap<String, Integer> lemmaMap, int siteId) {
        for (Map.Entry<String, Integer> lemma : lemmaMap.entrySet()) {
            String lemmaName = lemma.getKey();
            List<LemmaDAO> lemmaDAO1 = lemmaRepositoryService.getLemma(lemmaName);
            LemmaDAO lemmaDAO2 = lemmaDAO1.stream().
                    filter(lemma3 -> lemma3.getSiteId() == siteId).
                    findFirst().
                    orElse(null);
            if (lemmaDAO2 == null) {
                LemmaDAO newLemmaDAO = new LemmaDAO(lemmaName, 1, siteId);
                lemmaRepositoryService.save(newLemmaDAO);
            } else {
                int count = lemmaDAO2.getFrequency();
                lemmaDAO2.setFrequency(++count);
                lemmaRepositoryService.save(lemmaDAO2);
            }
        }
    }

    private TreeMap<String, Float> indexingLemmas(TreeMap<String, Integer> lemmas, float weight) {
        TreeMap<String, Float> map = new TreeMap<>();
        for (Map.Entry<String, Integer> lemma : lemmas.entrySet()) {
            String name = lemma.getKey();
            float w;
            if (!map.containsKey(name)) {
                w = (float) lemma.getValue() * weight;
            } else {
                w = map.get(name) + ((float) lemma.getValue() * weight);
            }
            map.put(name, w);
        }
        return map;
    }

    private void indexingToDB(TreeMap<String, Float> map, String path) {
        PageDAO pageDAO = pageRepositoryService.getPage(path);
        int pathId = pageDAO.getId();
        int siteId = pageDAO.getSiteId();
        for (Map.Entry<String, Float> lemma : map.entrySet()) {

            String lemmaName = lemma.getKey();
            List<LemmaDAO> lemmaDAO1 = lemmaRepositoryService.getLemma(lemmaName);
            for (LemmaDAO l : lemmaDAO1) {
                if (l.getSiteId() == siteId) {
                    int lemmaId = l.getId();
                    IndexingDAO indexingDAO = new IndexingDAO(pathId, lemmaId, lemma.getValue());
                    indexRepositoryService.save(indexingDAO);
                }
            }
        }
    }

    private void prepareDbToIndexing(PageDAO pageDAO) {
        List<IndexingDAO> indexingDAOList = indexRepositoryService.getAllIndexingByPageId(pageDAO.getId());
        List<LemmaDAO> allLemmasIdByPage = lemmaRepositoryService.findLemmaByIndexing(indexingDAOList);
        lemmaRepositoryService.deleteAllLemmas(allLemmasIdByPage);
        indexRepositoryService.deleteAllIndexing(indexingDAOList);
        pageRepositoryService.deletePage(pageDAO);
    }


}

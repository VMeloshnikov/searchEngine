package searchengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.response.SearchResponseService;
import searchengine.model.DAO.*;
import searchengine.services.IndexRepositoryService;
import searchengine.services.LemmaRepositoryService;
import searchengine.services.PageRepositoryService;
import searchengine.services.SiteRepositoryService;
import searchengine.services.morphological.MorphologicalAnalysis;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class Search {

    private final SiteRepositoryService siteRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;

    public Search(SiteRepositoryService siteRepositoryService,
                  IndexRepositoryService indexRepositoryService,
                  PageRepositoryService pageRepositoryService,
                  LemmaRepositoryService lemmaRepositoryService) {
        this.siteRepositoryService = siteRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
    }

    public SearchResponseService searchService(RequestDAO requestDAO, String url, int offset, int limit) {
        List<SiteDAO> siteDAOList = siteRepositoryService.getAllSites();
        List<SearchDataDAO> searchDataDAOList = new ArrayList<>();
        if (url == null) {
            for (SiteDAO s : siteDAOList) {
                Map<PageDAO, Double> list = searching(requestDAO, s.getId());

                searchDataDAOList.addAll(getSortedSearchData(list, requestDAO));
            }
        } else {
            SiteDAO siteDAO = siteRepositoryService.getSite(url);
            Map<PageDAO, Double> list = searching(requestDAO, siteDAO.getId());
            searchDataDAOList.addAll(getSortedSearchData(list, requestDAO));
        }
        int count;
        searchDataDAOList.sort(Comparator.comparingDouble(SearchDataDAO::getRelevance));
        if (searchDataDAOList.isEmpty()) {
            return new SearchResponseService(false);
        }
        if (limit + offset < searchDataDAOList.size()) {
            count = limit;
        } else {
            count = searchDataDAOList.size() - offset;
        }
        SearchDataDAO[] searchData = new SearchDataDAO[count];
        for (int i = offset; i < count; i++) {
            searchData[i] = searchDataDAOList.get(i);
        }
        return new SearchResponseService(true, count, searchData);
    }

    private Map<PageDAO, Double> searching(RequestDAO requestDAO, int siteId) {
        HashMap<PageDAO, Double> pageRelevance = new HashMap<>();
        List<LemmaDAO> reqLemmaDAOs = sortedReqLemmas(requestDAO, siteId);
        List<Integer> pageIndexes = new ArrayList<>();
        if (!reqLemmaDAOs.isEmpty()) {
            List<IndexingDAO> indexingDAOList = indexRepositoryService.
                    getAllIndexingByLemmaId(reqLemmaDAOs.get(0).getId());
            indexingDAOList.forEach(indexing -> pageIndexes.add(indexing.getPageId()));
            for (LemmaDAO lemmaDAO : reqLemmaDAOs) {
                if (!pageIndexes.isEmpty() && lemmaDAO.getId() != reqLemmaDAOs.get(0).getId()) {
                    List<IndexingDAO> indexingDAOList2 = indexRepositoryService.getAllIndexingByLemmaId
                            (lemmaDAO.getId());
                    List<Integer> tempList = new ArrayList<>();
                    indexingDAOList2.forEach(indexing -> tempList.add(indexing.getPageId()));
                    pageIndexes.retainAll(tempList);
                }
            }
            Map<PageDAO, Double> pageAbsRelevance = new HashMap<>();

            double maxRel = 0.0;
            for (Integer p : pageIndexes) {
                Optional<PageDAO> opPage;
                opPage = pageRepositoryService.findPageByPageIdAndSiteId(p, siteId);
                if (opPage.isPresent()) {
                    PageDAO pageDAO = opPage.get();
                    double r = getAbsRelevance(pageDAO, reqLemmaDAOs);
                    pageAbsRelevance.put(pageDAO, r);
                    if (r > maxRel)
                        maxRel = r;
                }
            }
            for (Map.Entry<PageDAO, Double> abs : pageAbsRelevance.entrySet()) {
                pageRelevance.put(abs.getKey(), abs.getValue() / maxRel);
            }
        }
        return pageRelevance;
    }

    private double getAbsRelevance(PageDAO pageDAO, List<LemmaDAO> lemmaDAOS) {
        double r = 0.0;
        int pageId = pageDAO.getId();
        for (LemmaDAO lemmaDAO : lemmaDAOS) {
            int lemmaId = lemmaDAO.getId();
            IndexingDAO indexingDAO = indexRepositoryService.getIndexing(lemmaId, pageId);
            r = r + indexingDAO.getRanking();
        }
        return r;
    }

    private List<SearchDataDAO> getSortedSearchData(Map<PageDAO, Double> sortedPageMap, RequestDAO requestDAO) {
        List<SearchDataDAO> responses = new ArrayList<>();
        LinkedHashMap<PageDAO, Double> sortedByRankPages =
                (LinkedHashMap<PageDAO, Double>) sortMapByValue(sortedPageMap);
        for (Map.Entry<PageDAO, Double> page : sortedByRankPages.entrySet()) {
            SearchDataDAO response = getResponseByPage(page.getKey(), requestDAO, page.getValue());
            responses.add(response);
        }
        return responses;
    }

    public <K, V extends Comparable<? super V>> Map<K, V>
    sortMapByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    private List<LemmaDAO> sortedReqLemmas(RequestDAO requestDAO, int siteId) {
        List<LemmaDAO> lemmaDAOList = new ArrayList<>();
        List<String> list = requestDAO.getReqLemmas();
        for (String s : list) {
            List<LemmaDAO> reqLemmaDAOS = lemmaRepositoryService.getLemma(s);
            for (LemmaDAO l : reqLemmaDAOS) {
                if (l.getSiteId() == siteId) {
                    lemmaDAOList.add(l);
                }
            }
        }
        lemmaDAOList.sort(Comparator.comparingInt(LemmaDAO::getFrequency));
        return lemmaDAOList;
    }

    private SearchDataDAO getResponseByPage(PageDAO pageDAO, RequestDAO requestDAO, double relevance) {
        SearchDataDAO response = new SearchDataDAO();
        SiteDAO siteDAO = siteRepositoryService.getSite(pageDAO.getSiteId());
        String siteUrl = siteDAO.getUrl();
        String siteName = siteDAO.getName();
        String uri = pageDAO.getPath();
        String title = getTitle(pageDAO.getContent());
        String snippet = getSnippet(pageDAO.getContent(), requestDAO);
        response.setSite(siteUrl);
        response.setSiteName(siteName);
        response.setRelevance(relevance);
        response.setUri(uri);
        response.setTitle(title);
        response.setSnippet(snippet);
        return response;
    }

    private String getTitle(String html) {
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select("title");
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (!(builder.length() < 1)) {
            string = builder.toString();
        }
        return string;
    }

    private String getSnippet(String html, RequestDAO requestDAO) {
        MorphologicalAnalysis analyzer = new MorphologicalAnalysis();
        String string = "";
        Document document = Jsoup.parse(html);
        Elements titleElements = document.select("title");
        Elements bodyElements = document.select("body");
        StringBuilder builder = new StringBuilder();
        titleElements.forEach(element -> builder.append(element.text()).append(" ").append("\n"));
        bodyElements.forEach(element -> builder.append(element.text()).append(" "));
        if (!(builder.length() < 1)) {
            string = builder.toString();
        }
        List<String> req = requestDAO.getReqLemmas();
        Set<Integer> integerList = new TreeSet<>();
        for (String s : req) {
            integerList.addAll(analyzer.findLemmaIndexInText(string, s));
        }
        List<TreeSet<Integer>> indexesList = getSearchingIndexes(string, integerList);
        StringBuilder builder1 = new StringBuilder();
        for (TreeSet<Integer> set : indexesList) {
            int from = set.first();
            int to = set.last();
            Pattern pattern = Pattern.compile("\\p{Punct}|\\s");
            Matcher matcher = pattern.matcher(string.substring(to));
            int offset = 0;
            if (matcher.find()) {
                offset = matcher.end();
            }
            builder1.append("<b>")
                    .append(string, from, to + offset)
                    .append("</b>");
            if (!((string.length() - to) < 30)) {
                builder1.append(string, to + offset, string.indexOf(" ", to + offset + 30))
                        .append("... ");
            }
        }
        return builder1.toString();
    }

    private List<TreeSet<Integer>> getSearchingIndexes(String string, Set<Integer> indexesOfBolt) {
        ArrayList<Integer> indexes = new ArrayList<>(indexesOfBolt);
        List<TreeSet<Integer>> list = new ArrayList<>();
        TreeSet<Integer> temp = new TreeSet<>();
        for (int i = 0; i < indexes.size(); i++) {
            String s = string.substring(indexes.get(i));
            int end = s.indexOf(" ");
            if ((i + 1) <= indexes.size() - 1 && (indexes.get(i + 1) - indexes.get(i)) < end + 5) {
                temp.add(indexes.get(i));
                temp.add(indexes.get(i + 1));
            } else {
                if (!temp.isEmpty()) {
                    list.add(temp);
                    temp = new TreeSet<>();
                }
                temp.add(indexes.get(i));
                list.add(temp);
                temp = new TreeSet<>();
            }
        }
        list.sort((Comparator<Set<Integer>>) (o1, o2) -> o2.size() - o1.size());
        ArrayList<TreeSet<Integer>> searchingIndexes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            if (list.size() > i) {
                searchingIndexes.add(list.get(i));
            }
        }
        return searchingIndexes;
    }
}

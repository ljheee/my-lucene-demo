package com.ljheee.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SearchService {

    String indexDir = "/Users/lijianhua/Documents/lucene_index";


    public List<Code> doSearch(String key, int limit) throws Exception {
        ArrayList<Code> list = new ArrayList<>();


        FSDirectory fsDirectory = FSDirectory.open(new File(indexDir).toPath());
        IndexReader reader = DirectoryReader.open(fsDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        Query query = new QueryParser("contents", analyzer).parse(key);
        TopDocs top = searcher.search(query, limit);

        System.out.println("命中数totalHits: " + top.totalHits);
        for (ScoreDoc scoreDoc : top.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            String name = document.get("fileName");
            String path = document.get("filePath");
            String text = document.get("contents");
            list.add(new Code(name, path, highLight(text, analyzer, query, name)));
        }

        reader.close();
        fsDirectory.close();
        return list;
    }

    private String highLight(String text, Analyzer analyzer, Query query, String fileName) throws IOException, InvalidTokenOffsetsException {
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(text.length()));
        TokenStream tokenStream = analyzer.tokenStream(fileName, new FileReader(text));
        String highlighterText = highlighter.getBestFragment(tokenStream, text);

        return highlighterText;
    }

    public static void main(String[] args) throws Exception {
        SearchService searchService = new SearchService();
        List<Code> codes = searchService.doSearch("DispatcherServlet", 100);
        System.out.println(codes);
    }

}

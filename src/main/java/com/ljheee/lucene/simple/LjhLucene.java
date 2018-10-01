package com.ljheee.lucene.simple;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;


/**
 * 使用lucene，完成索引CRUD
 */
public class LjhLucene {


    // 指定lucene生成索引文件的 存放路径
    public static final String FILE_PATH = "/Users/lijianhua/Documents/lucene_index";

    /**
     * 创建lucene索引
     * TextField字段 根据业务需要，可自定义多个
     *
     * @param name
     * @param describe
     * @throws IOException
     */
    public void createIndex(String name, String describe) throws IOException {
        FSDirectory fsDirectory =
                FSDirectory.open(new File(FILE_PATH).toPath());
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(fsDirectory, config);
        Document document = new Document();
        document.add(new TextField("name", name, Field.Store.YES));
        document.add(new TextField("describe", describe, Field.Store.YES));
        writer.addDocument(document);
        writer.commit();
        writer.close();
        fsDirectory.close();
    }


    /**
     * [从索引]查找
     *
     * @param keyWord
     * @throws IOException
     * @throws ParseException
     */
    public void search(String keyWord) throws IOException, ParseException {
        FSDirectory fsDirectory = FSDirectory.open(new File(FILE_PATH).toPath());
        IndexReader reader = DirectoryReader.open(fsDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new QueryParser("describe", new StandardAnalyzer()).parse(keyWord);
        TopDocs top = searcher.search(query, 100);
        System.out.println("命中数totalHits: " + top.totalHits);
        for (ScoreDoc scoreDoc : top.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            System.out.println(document.get("name"));
            System.out.println(document.get("describe"));
        }
    }


    /**
     * 获取 docId 文档
     *
     * @param docId
     * @throws IOException
     */
    public void get(int docId) throws IOException {
        FSDirectory fsDirectory =
                FSDirectory.open(new File(FILE_PATH).toPath());
        IndexReader reader = DirectoryReader.open(fsDirectory);
        System.out.println(reader.numDocs());
        Document doc = reader.document(docId);
        System.out.println(doc.get("name"));
        System.out.println(doc.get("describe"));

    }


    /**
     * 删除lucene索引[逻辑删除]
     *
     * @param name
     * @throws IOException
     */
    public void delete(String name) throws IOException {

        FSDirectory fsDirectory = FSDirectory.open(new File(FILE_PATH).toPath());
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(fsDirectory, config);
        writer.deleteDocuments(new Term("name", name));
        writer.commit();
        writer.close();
        fsDirectory.close();
    }


    public static void main(String[] args) throws IOException, ParseException {
        LjhLucene lucene = new LjhLucene();

        // 数据源：此处直接硬编码；事件应用中可从各处获取
//        lucene.createIndex("ljh","ljh is a very good student!");
//        lucene.createIndex("yyl","yyl is the xxx of ljh,a very good student,too!");


        lucene.search("yyl");
        lucene.get(0);


    }
}

package com.ljheee.lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * 为spring源码文件创建索引，实现代码搜索
 */
public class Indexer {

    private IndexWriter writer = null;

    /**
     * 创建IndexWriter，
     *
     * @param indexDir 存放lucene的索引目录
     */
    public Indexer(String indexDir) {

        try {
            Directory directory = FSDirectory.open(new File(indexDir).toPath());
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            writer = new IndexWriter(directory, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    public int getDocCount() {
        return writer.numDocs();
    }

    public void index(String dataDir) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {

            if (file.isFile() && file.getName().endsWith(".java")) {
                indexFile(file);
            } else if (file.isDirectory()) {
                index(file.getPath());
            }
        }

    }

    private void indexFile(File f) throws IOException {

        //输出 文件的路径
        System.out.println("索引文件:" + f.getCanonicalPath());
        //获取文档，文档里设置每个字段
        Document doc = getDocument(f);
        // 开始写入（就是把文档写进索引文件里去了）
        writer.addDocument(doc);
    }

    private Document getDocument(File f) throws IOException {

        Document document = new Document();
        document.add(new TextField("fileName", f.getName(), Field.Store.YES));
        document.add(new TextField("filePath", f.getCanonicalPath(), Field.Store.YES));
        document.add(new TextField("contents", new String(Files.readAllBytes(f.toPath())), Field.Store.YES));
        return document;
    }


    /**
     *  为源文件创建索引
     * @param args
     */
    public static void main(String[] args) {


        String indexDir = "/Users/lijianhua/Documents/lucene_index";
        String dataDir = "/Users/lijianhua/Documents/";


        int numIndexed = 0;
        long start = System.currentTimeMillis();


        Indexer indexer = new Indexer(indexDir);

        try {
            indexer.index(dataDir);
            numIndexed = indexer.getDocCount();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                indexer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("索引" + numIndexed + "个文件，花费：" + (end - start));
    }


}

package com.ljheee.lucene.index;

/**
 * Created by lijianhua04 on 2018/10/1.
 */
public class Code {

    public String fileName;
    public String filePath;
    public String content;


    public Code(String fileName, String filePath, String content) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Code{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

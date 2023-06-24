package com.gm.webbackend.bo;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Document(collection="qestions")
public class QuestionBO {
    @Id
    String id;

    String sectorcode;

    String title;

    Date createdate;

    String author;

    String content;

    String sourceip;

    String logosrc;

    List filesrc;

    int remarksnum;

    String createDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceip() {
        return sourceip;
    }

    public void setSourceip(String sourceip) {
        this.sourceip = sourceip;
    }

    public String getLogosrc() {
        return logosrc;
    }

    public void setLogosrc(String logosrc) {
        this.logosrc = logosrc;
    }

    public int getRemarksnum() {
        return remarksnum;
    }

    public void setRemarksnum(int remarksnum) {
        this.remarksnum = remarksnum;
    }

    public String getSectorcode() {
        return sectorcode;
    }

    public void setSectorcode(String sectorcode) {
        this.sectorcode = sectorcode;
    }

    public List getFilesrc() {
        return filesrc;
    }

    public void setFilesrc(List filesrc) {
        this.filesrc = filesrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}

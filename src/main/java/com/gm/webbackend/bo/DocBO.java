package com.gm.webbackend.bo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Document(collection="docs")
//@Data
public class DocBO {

    @Id
    String id;

    String code;

    String name;

    int section;

    String sectorcode;

    Date createdate;

    String srcaddr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getSectorcode() {
        return sectorcode;
    }

    public void setSectorcode(String sectorcode) {
        this.sectorcode = sectorcode;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getSrcaddr() {
        return srcaddr;
    }

    public void setSrcaddr(String srcaddr) {
        this.srcaddr = srcaddr;
    }
}

package com.gm.webbackend.ao;

import java.util.List;
//import lombok.Data;
//@Data
public class TreeSectorAO {
    String id;
    String label;
    String data;
    List<TreeSectorAO> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<TreeSectorAO> getChildren() {
        return children;
    }

    public void setChildren(List<TreeSectorAO> children) {
        this.children = children;
    }
}

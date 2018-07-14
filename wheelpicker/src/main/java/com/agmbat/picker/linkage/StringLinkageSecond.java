package com.agmbat.picker.linkage;

import java.util.ArrayList;
import java.util.List;

public class StringLinkageSecond implements LinkageSecond<String> {

    private String name;
    private List<String> thirds = new ArrayList<>();

    public StringLinkageSecond(String name, List<String> thirds) {
        this.name = name;
        this.thirds = thirds;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getId() {
        return name;
    }

    @Override
    public List<String> getThirds() {
        return thirds;
    }

}
package com.agmbat.picker.linkage;

import java.util.ArrayList;
import java.util.List;

public class StringLinkageFirst implements LinkageFirst<StringLinkageSecond> {

    private String name;
    private List<StringLinkageSecond> seconds = new ArrayList<>();

    public StringLinkageFirst(String name, List<StringLinkageSecond> seconds) {
        this.name = name;
        this.seconds = seconds;
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
    public List<StringLinkageSecond> getSeconds() {
        return seconds;
    }
}
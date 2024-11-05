package com.example.pmg302_project.util;

import java.util.List;

public class ApiResponse {
    private List<String> sizes;
    private List<String> colors;

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }
}

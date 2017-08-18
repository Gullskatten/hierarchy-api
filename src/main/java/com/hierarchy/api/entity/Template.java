package com.hierarchy.api.entity;

public class Template {

    String template;
    String defaultName;

    public Template(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}

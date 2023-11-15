package com.jiand.annotation.meta.route;

/**
 * @author jiand
 */
public class AutowiredInfo {
    private final String name;
    private final String fieldName;

    public AutowiredInfo(String name, String fieldName) {
        this.name = name;
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }
    public String getFieldName() {
        return fieldName;
    }
}

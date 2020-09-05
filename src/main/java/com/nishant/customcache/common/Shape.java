package com.nishant.customcache.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author cannot disclose
 */
public class Shape implements Serializable {
    private static final long serialVersionUID = 20190326L;

    private final Integer shapeId;
    private final String name;

    public Shape(Integer shapeId, String name) {
        this.shapeId = shapeId;
        this.name = name;
    }

    public Integer getShapeId() {
        return shapeId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape = (Shape) o;
        return Objects.equals(shapeId, shape.shapeId) &&
                Objects.equals(name, shape.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shapeId, name);
    }

    @Override
    public String toString() {
        return "Shape{" +
                "shapeId=" + shapeId +
                ", name='" + name + '\'' +
                '}';
    }
}

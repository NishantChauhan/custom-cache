package com.nishant.customcache.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author cannot disclose
 */
public class Rectangle extends Shape implements Serializable {
    private static final long serialVersionUID = 20190326L;

    private final Integer length;
    private final Integer breadth;

    public Rectangle(Integer shapeId, String name, Integer length, Integer breadth) {
        super(shapeId, name);
        this.length = length;
        this.breadth = breadth;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getBreadth() {
        return breadth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Rectangle rectangle = (Rectangle) o;
        return Objects.equals(length, rectangle.length) &&
                Objects.equals(breadth, rectangle.breadth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), length, breadth);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "shapeId=" + super.getShapeId() +
                ", name='" + super.getName() +
                ", length=" + length +
                ", breadth=" + breadth +
                '}';
    }
}

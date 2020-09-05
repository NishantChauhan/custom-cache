package com.nishant.customcache.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author cannot disclose
 */
public class ShapeKey implements Serializable {
    private static final long serialVersionUID = 20190326L;

    private final Integer shapeId;

    public ShapeKey(Integer shapeId) {
        this.shapeId = shapeId;
    }

    public Integer getShapeId() {
        return shapeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShapeKey shapeKey = (ShapeKey) o;
        return Objects.equals(shapeId, shapeKey.shapeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shapeId);
    }

    @Override
    public String toString() {
        return "ShapeKey{" +
                "shapeId=" + shapeId +
                '}';
    }
}

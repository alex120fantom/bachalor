package ua.kh.khpi.alex_babenko.art.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Line implements Serializable {

    private List<BigDecimal> lineValue;

    public List<BigDecimal> getLineValue() {
        return lineValue;
    }

    public void setLineValue(List<BigDecimal> lineValue) {
        this.lineValue = lineValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (!lineValue.equals(line.lineValue)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return lineValue.hashCode();
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineValue=" + lineValue +
                '}';
    }
}

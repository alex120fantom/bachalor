package ua.kh.khpi.alex_babenko.art.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class DefaultKnowledge implements Knowledge {

    private List<Line> b; // коефициенты весов
    private List<Line> t; // значения
    private List<Line> bCopy = new ArrayList<>(); // коефициенты весов;
    private List<Line> tCopy = new ArrayList<>(); // значения

    @Override
    public List<Line> getB() {
        return b;
    }

    @Override
    public void setB(List<Line> b) {
        this.b = b;
    }

    @Override
    public List<Line> getT() {
        return t;
    }

    @Override
    public void setT(List<Line> t) {
        this.t = t;
    }

    @Override
    public List<Line> getbCopy() {
        return bCopy;
    }

    @Override
    public void setbCopy(List<Line> bCopy) {
        this.bCopy = bCopy;
    }

    @Override
    public List<Line> gettCopy() {
        return tCopy;
    }

    @Override
    public void settCopy(List<Line> tCopy) {
        this.tCopy = tCopy;
    }

    @Override
    public String toString() {
        return "DefaultKnowledge{" +
                "b=" + b +
                ", t=" + t +
                ", bCopy=" + bCopy +
                ", tCopy=" + tCopy +
                '}';
    }
}

package ua.kh.khpi.alex_babenko.art.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class KnowledgeBase {

    private List<Line> b; // коефициенты весов
    private List<Line> t; // значения
    private List<Line> bCopy = new ArrayList<>(); // коефициенты весов;
    private List<Line> tCopy = new ArrayList<>(); // значения

    public List<Line> getB() {
        return b;
    }

    public void setB(List<Line> b) {
        this.b = b;
    }

    public List<Line> getT() {
        return t;
    }

    public void setT(List<Line> t) {
        this.t = t;
    }

    public List<Line> getbCopy() {
        return bCopy;
    }

    public void setbCopy(List<Line> bCopy) {
        this.bCopy = bCopy;
    }

    public List<Line> gettCopy() {
        return tCopy;
    }

    public void settCopy(List<Line> tCopy) {
        this.tCopy = tCopy;
    }
}

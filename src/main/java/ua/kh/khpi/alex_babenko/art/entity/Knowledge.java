package ua.kh.khpi.alex_babenko.art.entity;

import java.util.List;

public interface Knowledge {

    List<Line> getB();
    void setB(List<Line> b);

    List<Line> getT();
    void setT(List<Line> t);

    List<Line> getbCopy();
    void setbCopy(List<Line> bCopy);

    List<Line> gettCopy();
    void settCopy(List<Line> tCopy);

}

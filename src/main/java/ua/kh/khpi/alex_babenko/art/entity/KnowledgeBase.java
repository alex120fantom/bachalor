package ua.kh.khpi.alex_babenko.art.entity;

public class KnowledgeBase {

    private double[][] b; // коефициенты весов
    private double[][] t; // значения
    private double[][] bCopy; // коефициенты весов;
    private double[][] tCopy; // значения

    public double[][] getB() {
        return b;
    }

    public void setB(double[][] b) {
        this.b = b;
    }

    public double[][] getT() {
        return t;
    }

    public void setT(double[][] t) {
        this.t = t;
    }

    public double[][] getbCopy() {
        return bCopy;
    }

    public void setbCopy(double[][] bCopy) {
        this.bCopy = bCopy;
    }

    public double[][] gettCopy() {
        return tCopy;
    }

    public void settCopy(double[][] tCopy) {
        this.tCopy = tCopy;
    }
}

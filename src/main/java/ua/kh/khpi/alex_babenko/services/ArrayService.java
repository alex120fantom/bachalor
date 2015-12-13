package ua.kh.khpi.alex_babenko.services;

import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.util.List;

public interface ArrayService {

    List<Line> createCopy(List<Line> source);

    List<Line> buildLineMatrix(int height, int width, double value);
}

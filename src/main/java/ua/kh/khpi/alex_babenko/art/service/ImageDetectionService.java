package ua.kh.khpi.alex_babenko.art.service;

import ua.kh.khpi.alex_babenko.art.entity.Knowledge;

import java.util.List;

public interface ImageDetectionService {
    List<Double[]> findViruses(double[][] potentialViruses, Knowledge knowledge);
}

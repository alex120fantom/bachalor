package ua.kh.khpi.alex_babenko.services;

import java.io.IOException;

public interface FileService {
    double[][] readMatrixFromFile(String filename) throws IOException;

    int countLines(String filename) throws IOException;

    int countLineSize(String filename) throws IOException;
}

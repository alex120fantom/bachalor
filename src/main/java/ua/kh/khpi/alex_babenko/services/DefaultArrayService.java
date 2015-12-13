package ua.kh.khpi.alex_babenko.services;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.Line;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class DefaultArrayService implements ArrayService {

	@Override
    public List<Line> createCopy(List<Line> source) {
        return source.stream()
                .map(SerializationUtils::clone)
                .collect(toList());
    }


    @Override
    public List<Line> buildLineMatrix(int height, int width, double value) {

        Line line = new Line();
        List<BigDecimal> lineValue = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            lineValue.add(BigDecimal.valueOf(value));
        }
        line.setLineValue(lineValue);

        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            lines.add(SerializationUtils.clone(line));
        }
        return lines;
    }

}

package com.devmind.measuregenerator;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

@RestController
@RequestMapping("/measures")
public class MeasureController {

    @GetMapping
    public ResponseEntity<List<MeasureDTO>> computeMeasures(@RequestParam Instant start,
                                                            @RequestParam Instant end,
                                                            @RequestParam int min,
                                                            @RequestParam int max,
                                                            @RequestParam int step) {

        if (start.isAfter(end)) {
            throw new MeasureException("start must be less than end");

        }
        if (min > max) {
            throw new MeasureException("min must be less than max");

        }
        if (step < 1) {
            throw new MeasureException("step is a value > 0");
        }

        // We compute the step number between start and end instants
        Duration duration = Duration.between(start, end);
        Long nbOfSteps = (duration.getSeconds() / step) + 1;
        int nbPeak = getNbPeak(nbOfSteps.intValue());

        // We generate a matrix with one value for each steps
        List<Power> points = IntStream.range(0, nbOfSteps.intValue())
                                      .mapToObj(i -> new Power(getRandomNumberInts(min, max), i % nbPeak))
                                      .sorted(comparing(Power::getRank).thenComparing(Power::getValueForComparing))
                                      .collect(Collectors.toList());

        Instant current = start;
        List<MeasureDTO> measures = new ArrayList<>();
        int i = 0;
        while (current.isBefore(end)) {
            measures.add(new MeasureDTO(current, points.get(i).valueInWatt));
            current = current.plusSeconds(step);
            i++;
        }

        return ResponseEntity.ok(measures);
    }


    public int getRandomNumberInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, (max + 1)).findFirst().orElse(0);
    }

    public int getNbPeak(int nbOfSteps) {
        Random random = new Random();
        return nbOfSteps / random.ints(1, nbOfSteps / 3).findFirst().orElse(1);
    }

    private static class Power {
        private final int valueInWatt;
        private final int rank;
        private final int sign;

        public Power(int valueInWatt, int rank) {
            this.valueInWatt = valueInWatt;
            this.rank = rank;
            this.sign = rank % 2 == 0 ? 1 : -1;
        }

        public int getRank() {
            return rank;
        }

        public int getValueForComparing() {
            return sign * valueInWatt;
        }
    }
}

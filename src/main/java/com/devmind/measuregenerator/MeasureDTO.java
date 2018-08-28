package com.devmind.measuregenerator;

import java.time.Instant;

public class MeasureDTO {

    private final Instant instant;
    private final Integer valueInWatt;

    public MeasureDTO(Instant instant, Integer valueInWatt) {
        this.instant = instant;
        this.valueInWatt = valueInWatt;
    }

    public Instant getInstant() {
        return instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }
}

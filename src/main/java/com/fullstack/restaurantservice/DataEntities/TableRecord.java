package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record TableRecord(@NotNull Integer tableNumber, @NotNull Integer capacity) {
    public boolean equals(TableRecord comparedRecord){
        return this.tableNumber.equals(comparedRecord.tableNumber) &&
        this.capacity.equals(comparedRecord.capacity);
    }
}

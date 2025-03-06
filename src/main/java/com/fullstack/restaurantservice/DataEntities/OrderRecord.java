package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRecord(@NotNull String firstName, @NotNull Integer tableNumber, @NotNull String dish, Float bill) {
    public boolean equals(OrderRecord comparedRecord) {
        return (this.firstName.equals(comparedRecord.firstName) &&
        this.tableNumber.equals(comparedRecord.tableNumber) &&
        this.dish.equals(comparedRecord.dish) &&
        this.bill.equals(comparedRecord.bill));
    }
}

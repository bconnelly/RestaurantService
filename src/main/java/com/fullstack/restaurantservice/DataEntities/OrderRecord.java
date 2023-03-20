package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRecord(String firstName, Integer tableNumber, String dish, Float bill) {
    public boolean equals(OrderRecord returnedOrder) {
        return (this.firstName.equals(returnedOrder.firstName()) &&
        this.tableNumber.equals(returnedOrder.tableNumber) &&
        this.dish.equals(returnedOrder.dish) &&
        this.bill.equals(returnedOrder.bill));
    }
}

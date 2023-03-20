package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerRecord(String firstName, String address, Float cash, Integer tableNumber) {

    public boolean equals(CustomerRecord returnedCustomer) {
        return (this.firstName.equals(returnedCustomer.firstName) &&
                this.address.equals(returnedCustomer.address) &&
                this.cash.equals(returnedCustomer.cash) &&
                this.tableNumber.equals((returnedCustomer.tableNumber)));
    }
}

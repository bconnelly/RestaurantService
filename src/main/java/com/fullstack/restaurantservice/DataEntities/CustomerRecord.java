package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerRecord(String firstName, String address, Float cash, Integer tableNumber) {

    public boolean equals(CustomerRecord returnedCustomer) {
        if(this.firstName != null){
            if(!this.firstName.equals(returnedCustomer.firstName)) return false;
        }
        if(this.address != null){
            if(!this.address.equals(returnedCustomer.address)) return false;
        }
        if(this.cash != null){
            if(!this.cash.equals(returnedCustomer.cash)) return false;
        }
        if(this.tableNumber != null){
            return this.tableNumber.equals(returnedCustomer.tableNumber);
        }
        return true;
    }
}

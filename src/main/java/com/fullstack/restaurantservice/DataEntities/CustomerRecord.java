package com.fullstack.restaurantservice.DataEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerRecord(@NotNull String firstName, @NotNull String address, @NotNull Float cash, @Nullable Integer tableNumber) {

    public boolean equals(CustomerRecord comparedRecord) {

        if (!this.firstName.equals(comparedRecord.firstName) ||
                !this.address.equals(comparedRecord.address) ||
                !this.cash.equals(comparedRecord.cash)) {
            return false;
        }

        if(this.tableNumber != null){
            return this.tableNumber.equals(comparedRecord.tableNumber);
        } else {
            return true;
        }

    }
}

package com.fullstack.restaurantservice;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantServiceApplicationTest {

    RestaurantServiceApplication restaurantApi = new RestaurantServiceApplication();

    @Test
    void defaultLandingPage() {
        assert (restaurantApi.defaultLandingPage().getBody() != null);
        assert (Objects.equals(restaurantApi.defaultLandingPage().getBody(), "default landing page"));
    }

    @Test
    void seatCustomer() throws EntityNotFoundException {
//        assertThrows(EntityNotFoundException.class, () -> restaurantApi.seatCustomer("unit-test", "unit-test", null));
    }

    @Test
    void getOpenTables() {
    }

    @Test
    void submitOrder() {
    }

    @Test
    void bootCustomer() {
    }
}
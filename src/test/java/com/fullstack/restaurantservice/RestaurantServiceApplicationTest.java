package com.fullstack.restaurantservice;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;
import com.fullstack.restaurantservice.DataEntities.OrderRecord;
import com.fullstack.restaurantservice.DataEntities.TableRecord;
import com.fullstack.restaurantservice.DomainLogic.RestaurantLogic;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RestaurantServiceApplicationTest {

    @InjectMocks
    private RestaurantServiceApplication application;

    @Mock
    private RestaurantLogic restaurantLogicMock;

    @Test
    void seatCustomerTest() throws EntityNotFoundException {
        CustomerRecord expected = CustomerRecord.builder().firstName("alice").address("my address").cash(15.99f)
                .tableNumber(1).build();
        CustomerRecord inCustomer = CustomerRecord.builder().firstName("alice").address("my address").cash(15.99f).build();
        when(restaurantLogicMock.seatCustomer(inCustomer)).thenReturn(expected);

        CustomerRecord response = application.seatCustomer(inCustomer);

        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).seatCustomer(inCustomer);
    }

    @Test
    void seatGroupTest() throws EntityNotFoundException {
        List<CustomerRecord> expected = new ArrayList<>();
        expected.add(CustomerRecord.builder().firstName("alice").address("my address").cash(15.99f).build());
        expected.add(CustomerRecord.builder().firstName("bob").address("my address").cash(15.99f).build());
        when(restaurantLogicMock.seatGroup(expected)).thenReturn(expected);

        List<CustomerRecord> response = application.seatGroup(expected);

        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).seatGroup(expected);
    }

    @Test
    void submitOrderTest() throws EntityNotFoundException {
        OrderRecord expected = OrderRecord.builder().firstName("alice").dish("taco").bill(11.01f).tableNumber(1).build();
        when(restaurantLogicMock.submitOrder(expected)).thenReturn(expected);

        OrderRecord response = application.submitOrder(expected);
        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).submitOrder(expected);
    }

    @Test
    void serveOrder() {
        OrderRecord expected = OrderRecord.builder().firstName("alice").dish("taco").bill(11.01f).tableNumber(1).build();
        when(restaurantLogicMock.serveOrder("alice", 1)).thenReturn(expected);

        application.serveOrder("alice", 1);

        verify(restaurantLogicMock, times(1)).serveOrder("alice", 1);
    }

    @Test
    void bootCustomerTest() throws EntityNotFoundException {
        doNothing().when(restaurantLogicMock).bootCustomer("alice");

        application.bootCustomer("alice");

        verify(restaurantLogicMock, times(1)).bootCustomer("alice");
    }
}
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
    void seatCustomer() throws EntityNotFoundException {
        CustomerRecord expected = CustomerRecord.builder().firstName("alice").address("my address").cash(15.99f).build();
        when(restaurantLogicMock.seatCustomer(expected.firstName(), expected.address(), expected.cash())).thenReturn(expected);

        CustomerRecord response = application.seatCustomer(expected.firstName(), expected.address(), expected.cash());

        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).seatCustomer(expected.firstName(), expected.address(), expected.cash());
    }

    @Test
    void seatGroup() throws EntityNotFoundException {
        List<CustomerRecord> expected = new ArrayList<>();
        expected.add(CustomerRecord.builder().firstName("alice").address("my address").cash(15.99f).build());
        expected.add(CustomerRecord.builder().firstName("bob").address("my address").cash(15.99f).build());
        when(restaurantLogicMock.seatGroup(expected)).thenReturn(expected);

        List<CustomerRecord> response = application.seatGroup(expected);

        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).seatGroup(expected);
    }

    @Test
    void getOpenTables() throws EntityNotFoundException {
        List<TableRecord> expected = new ArrayList<>();
        expected.add(TableRecord.builder().tableNumber(1).capacity(4).build());
        expected.add(TableRecord.builder().tableNumber(2).capacity(5).build());
        when(restaurantLogicMock.getOpenTables()).thenReturn(expected);

        List<TableRecord> response = application.getOpenTables();

        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).getOpenTables();
    }

    @Test
    void submitOrder() throws EntityNotFoundException {
        OrderRecord expected = OrderRecord.builder().firstName("alice").dish("taco").bill(11.01f).tableNumber(1).build();
        when(restaurantLogicMock.submitOrder(expected.firstName(), expected.dish(), expected.tableNumber(), expected.bill())).thenReturn(expected);

        OrderRecord response = application.submitOrder(expected.firstName(), expected.dish(), expected.tableNumber(), expected.bill());
        assertEquals(expected, response);
        verify(restaurantLogicMock, times(1)).submitOrder(expected.firstName(), expected.dish(), expected.tableNumber(), expected.bill());
    }

    @Test
    void serveOrder() {
        OrderRecord expected = OrderRecord.builder().firstName("alice").dish("taco").bill(11.01f).tableNumber(1).build();
        when(restaurantLogicMock.serveOrder("alice", 1)).thenReturn(expected);

        application.serveOrder("alice", 1);

        verify(restaurantLogicMock, times(1)).serveOrder("alice", 1);
    }

    @Test
    void bootCustomer() throws EntityNotFoundException {
        CustomerRecord expected = CustomerRecord.builder().firstName("alice").address("fake st").cash(100.00f).tableNumber(1).build();
        when(restaurantLogicMock.bootCustomer("alice")).thenReturn(expected);

        application.bootCustomer("alice");

        verify(restaurantLogicMock, times(1)).bootCustomer("alice");
    }
}
package com.fullstack.restaurantservice.DomainLogic;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;
import com.fullstack.restaurantservice.DataEntities.OrderRecord;
import com.fullstack.restaurantservice.DataEntities.TableRecord;
import com.fullstack.restaurantservice.RestDataRetrieval.RestFetcher;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RestaurantLogicTest {

    @InjectMocks
    private RestaurantLogic restaurantLogic;

    @Mock
    private RestFetcher fetcherMock;

    private final List<TableRecord> expectedAllTables = new ArrayList<>(Arrays.asList(
            TableRecord.builder().tableNumber(1).capacity(4).build(),
            TableRecord.builder().tableNumber(2).capacity(4).build(),
            TableRecord.builder().tableNumber(3).capacity(6).build()));

    private final List<CustomerRecord> expectedAllCustomers = new ArrayList<>(Arrays.asList(
            CustomerRecord.builder().firstName("alice").address("test address1").tableNumber(1).cash(1.00f).build(),
            CustomerRecord.builder().firstName("bob").address("test address2").tableNumber(2).cash(15.25f).build(),
            CustomerRecord.builder().firstName("chuck").address("test address3").tableNumber(2).cash(100.01f).build()));

    @Test
    void seatCustomer() throws EntityNotFoundException {

        CustomerRecord expectedSeatedCustomer = CustomerRecord.builder()
                .firstName("dick").address("test address4").cash(9.87f).tableNumber(3).build();

        when(fetcherMock.seatCustomer("dick", "test address4", 9.87f, 3))
                .thenReturn(expectedSeatedCustomer);
        when(fetcherMock.getAllCustomers()).thenReturn(expectedAllCustomers);
        when(fetcherMock.getAllTables()).thenReturn(expectedAllTables);

        CustomerRecord returnedCustomer = restaurantLogic.seatCustomer("dick", "test address4", 9.87f);

        assert(expectedSeatedCustomer.equals(returnedCustomer));
        verify(fetcherMock, times(1)).getAllTables();
        verify(fetcherMock, times(1)).getAllCustomers();
        verify(fetcherMock, times(1)).seatCustomer(anyString(), anyString(), anyFloat(), anyInt());
    }

    @Test
    void seatGroup() throws EntityNotFoundException {

        CustomerRecord expectedCustomer1 = CustomerRecord.builder()
                .firstName("abc").address("abc").cash(10.10f).tableNumber(0).build();
        CustomerRecord expectedCustomer2 = CustomerRecord.builder()
                .firstName("xyz").address("abc").cash(10.10f).tableNumber(0).build();

        List<CustomerRecord> customers = Arrays.asList(expectedCustomer1, expectedCustomer2);
        when(fetcherMock.seatGroup(anyList())).thenReturn(customers);
        when(fetcherMock.getAllCustomers()).thenReturn(expectedAllCustomers);
        when(fetcherMock.getAllTables()).thenReturn(expectedAllTables);

        List<CustomerRecord> seated = restaurantLogic.seatGroup(customers);

        assert(customers.equals(seated));
        verify(fetcherMock, times(1)).getAllTables();
        verify(fetcherMock, times(1)).getAllCustomers();
        verify(fetcherMock, times(1)).seatGroup(anyList());

    }

    @Test
    void getOpenTablesTest() throws Exception {

        List<TableRecord> expectedOpenTables = new ArrayList<>(Collections.singletonList(
                TableRecord.builder().tableNumber(3).capacity(6).build()));

        when(fetcherMock.getAllCustomers()).thenReturn(expectedAllCustomers);
        when(fetcherMock.getAllTables()).thenReturn(expectedAllTables);

        List<TableRecord> returnedTables = restaurantLogic.getOpenTables();

        assert(expectedOpenTables.equals(returnedTables));
        verify(fetcherMock, times(1)).getAllTables();
        verify(fetcherMock, times(1)).getAllCustomers();
    }

    @Test
    void submitOrderTest() throws EntityNotFoundException {

        OrderRecord expectedRecord = OrderRecord.builder()
                .firstName("alice").dish("food").bill(10.00f).tableNumber(1).build();

        CustomerRecord customer = CustomerRecord.builder().firstName("alice").address("test address1")
                .cash(12.00f).tableNumber(1).build();

        when(fetcherMock.getCustomerByName("alice")).thenReturn(customer);
        when(fetcherMock.submitOrder("alice", "food", 1, 10.00f)).thenReturn(expectedRecord);

        OrderRecord returnedOrder = restaurantLogic.submitOrder("alice", "food", 1, 10.00f);


        assert(expectedRecord.equals(returnedOrder));
        verify(fetcherMock, times(1)).getCustomerByName(anyString());
        verify(fetcherMock, times(1)).submitOrder(anyString(), anyString(), anyInt(), anyFloat());

    }

    @Test
    void bootCustomerTest() throws EntityNotFoundException {

        CustomerRecord expected =
                new CustomerRecord("alice", "test address1", 1.00f, 1);

        when(fetcherMock.bootCustomer("alice")).thenReturn(expected);

        CustomerRecord ret = restaurantLogic.bootCustomer("alice");

        assert(ret.equals(expected));
        verify(fetcherMock, times(1)).bootCustomer("alice");
    }

    @Test
    void serveOrderTest(){

        OrderRecord expected = OrderRecord.builder().tableNumber(1).firstName("alice").dish("burger").bill(10.98f).build();
        when(fetcherMock.serveOrder("alice", 1)).thenReturn(expected);

        OrderRecord ret = restaurantLogic.serveOrder("alice", 1);

        assert(expected.equals(ret));

        verify(fetcherMock, times(1)).serveOrder("alice", 1);
    }
}
package com.fullstack.restaurantservice.RestDataRetrieval;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;

import com.fullstack.restaurantservice.DataEntities.OrderRecord;
import com.fullstack.restaurantservice.DataEntities.TableRecord;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RestFetcherTest {

    @Mock
    RestTemplate template;

    @Autowired
    RestFetcher restFetcher;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(restFetcher, "template", template);
    }

    @Test
    void getAllCustomersTest() throws EntityNotFoundException {
        CustomerRecord[] expectedResponse = {CustomerRecord.builder().firstName("test person")
                        .address("test address").cash(12.34f).tableNumber(1).build(),
                CustomerRecord.builder().firstName("another person")
                        .address("another address").cash(32.10f).tableNumber(2).build()};

        when(template.getForObject(anyString(), eq(CustomerRecord[].class)))
                .thenReturn(expectedResponse);

        List<CustomerRecord> response = restFetcher.getAllCustomers();

        verify(template, times(1)).getForObject(anyString(), eq(CustomerRecord[].class));
        assertEquals(new ArrayList<>(Arrays.asList(expectedResponse)), response);
    }

    @Test
    void getCustomerByNameTest() throws EntityNotFoundException {
        CustomerRecord expectedResponse = CustomerRecord.builder().firstName("test person")
                .address("test address").cash(9.87f).tableNumber(1).build();

        when(template.getForObject(anyString(), eq(CustomerRecord.class))).thenReturn(expectedResponse);

        CustomerRecord response = restFetcher.getCustomerByName("test person");

        verify(template, times(1)).getForObject(anyString(), eq(CustomerRecord.class));
        assertEquals(expectedResponse, response);
    }

    @Test
    void customerExistsTest() {
        Boolean expectedResponse = true;

        when(template.getForObject(anyString(), eq(Boolean.class))).thenReturn(expectedResponse);

        Boolean response = restFetcher.customerExists("test");

        verify(template, times(1)).getForObject(anyString(), eq(Boolean.class));
        assertEquals(expectedResponse, response);
    }

    @Test
    void seatCustomerTest() {
        CustomerRecord newCustomerRecord = CustomerRecord.builder()
                .firstName("test person").address("test address")
                .cash(54.32f).tableNumber(1).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CustomerRecord> request = new HttpEntity<>(newCustomerRecord, headers);

        when(template.postForObject(anyString(), eq(request), eq(CustomerRecord.class))).thenReturn(newCustomerRecord);

        CustomerRecord returnedCustomer = restFetcher.seatCustomer(newCustomerRecord);

        verify(template, times(1)).postForObject(anyString(), eq(request), eq(CustomerRecord.class));
        assertEquals(newCustomerRecord, returnedCustomer);
    }

    @Test
    void submitOrderTest() {
        OrderRecord newOrderRecord = OrderRecord.builder().firstName("test person")
                .dish("test dish").bill(1.23f).tableNumber(1).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderRecord> request = new HttpEntity<>(newOrderRecord, headers);

        when(template.postForObject(anyString(), eq(request), eq(OrderRecord.class))).thenReturn(newOrderRecord);

        OrderRecord returnedOrder = restFetcher.submitOrder(newOrderRecord);

        verify(template, times(1)).postForObject(anyString(), eq(request), eq(OrderRecord.class));
        assertEquals(newOrderRecord, returnedOrder);
    }

    @Test
    void getAllTablesTest() throws EntityNotFoundException {
        TableRecord[] expectedTables = {TableRecord.builder().tableNumber(1).capacity(2).build(),
                                    TableRecord.builder().tableNumber(2).capacity(2).build(),
                                    TableRecord.builder().tableNumber(3).capacity(4).build()};

        when(template.getForObject(anyString(), eq(TableRecord[].class))).thenReturn(expectedTables);

        List<TableRecord> returnedTables = restFetcher.getAllTables();

        verify(template, times(1)).getForObject(anyString(), eq(TableRecord[].class));
        assertEquals(new ArrayList<>(Arrays.asList(expectedTables)), returnedTables);
    }

    @Test
    void bootCustomerTest() {
        String misbehavingCustomer = "alice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(misbehavingCustomer, headers);

        doNothing().when(template).delete(anyString(), eq(request), eq(CustomerRecord.class));

        restFetcher.bootCustomer("alice");

        verify(template, times(1)).delete(anyString(), eq(request));
    }

    @Test
    void serveOrderTest(){
        OrderRecord orderToServe = OrderRecord.builder()
                .firstName("alice").dish("burger").bill(10.99f).tableNumber(1).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        when(template.postForObject(anyString(), eq(request), eq(OrderRecord.class))).thenReturn(orderToServe);

        OrderRecord servedOrder = restFetcher.serveOrder("alice", 1);

        assertEquals(orderToServe, servedOrder);
    }
}
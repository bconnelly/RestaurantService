package com.fullstack.restaurantservice.RestDataRetrieval;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;
import com.fullstack.restaurantservice.DataEntities.OrderRecord;
import com.fullstack.restaurantservice.DataEntities.TableRecord;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RestFetcher {

    public RestFetcher(){ this.template = new RestTemplate(); }
    private final RestTemplate template;

    @Value("${customers.host.url}")
    private String customersHost;
    @Value("${orders.host.url}")
    private String ordersHost;
    @Value("${tables.host.url}")
    private String tablesHost;
    @Value("${customer.get-all.endpoint}")
    private String customerGetAllUrl;
    @Value("${customer.get-by-name.endpoint}")
    private String customerGetByNameUrl;
    @Value("${customer.exists.endpoint}")
    private String customerExistsUrl;
    @Value("${customer.seat.endpoint}")
    private String customersSeatUrl;
    @Value("${customer.seat-group.endpoint}")
    private String customersSeatGroupUrl;
    @Value("${customer.boot.endpoint}")
    private String customersBootUrl;
    @Value("${order.submit.endpoint}")
    private String orderSubmitUrl;
    @Value("${order.serve.endpoint}")
    private String serveOrder;
    @Value("${table.get-all.endpoint}")
    private String tableGetAllUrl;

    public List<CustomerRecord> getAllCustomers() throws EntityNotFoundException {
        if(customersHost == null || customerGetAllUrl == null) throw new RuntimeException("failed to load environment");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customerGetAllUrl).toUriString();

        log.debug("calling customers /getAllCustomers");
        CustomerRecord[] customerRecords = template.getForObject(urlTemplate, CustomerRecord[].class);
        if(customerRecords != null) return new ArrayList<>(Arrays.asList(customerRecords));
        else throw new EntityNotFoundException("customer records could not be retrieved");
    }

    public CustomerRecord getCustomerByName(String firstName) throws EntityNotFoundException {
        if(customersHost == null || customerGetByNameUrl == null) throw new RuntimeException("failed to load environment");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customerGetByNameUrl)
                .queryParam("firstName", firstName).toUriString();

        log.debug("calling customers /getCustomerByFirstName");
        CustomerRecord customerRecord = template.getForObject(urlTemplate, CustomerRecord.class);
        if(customerRecord != null) return customerRecord;
        else throw new EntityNotFoundException("customer not found");
    }

    public Boolean customerExists(String firstName) {
        if(customersHost == null || customerExistsUrl == null)  throw new RuntimeException("failed to load environment");
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customerExistsUrl)
                .queryParam("firstName", firstName).toUriString();

        log.debug("calling customers /customerExists");
        return template.getForObject(urlTemplate, Boolean.class);
    }

    public CustomerRecord seatCustomer(CustomerRecord customer) {
        String firstName = customer.firstName();
        String address = customer.address();
        Float cash = customer.cash();
        Integer tableNumber = customer.tableNumber();

        if(customersHost == null || customersSeatUrl == null) throw new RuntimeException("failed to load environment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CustomerRecord customerRecord = CustomerRecord.builder()
                .firstName(firstName)
                .address(address)
                .cash(cash)
                .tableNumber(tableNumber)
                .build();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customersSeatUrl).toUriString();

        log.debug("calling customers /insertCustomer");
        HttpEntity<CustomerRecord> request = new HttpEntity<>(customerRecord, headers);
        return template.postForObject(urlTemplate, request, CustomerRecord.class);
    }

    public List<CustomerRecord> seatGroup(List<CustomerRecord> customers) {
        if(customersHost == null || customersSeatGroupUrl == null) throw new RuntimeException("failed to load environment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customersSeatGroupUrl).toUriString();

        log.debug("calling customers /insertGroup");
        HttpEntity<List<CustomerRecord>> request = new HttpEntity<>(customers, headers);

        return template.postForObject(urlTemplate, request, List.class);
    }

    public CustomerRecord bootCustomer(String misbehavingCustomer) {
        if(customersHost == null || customersBootUrl == null) throw new RuntimeException("failed to load environment");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(customersHost + customersBootUrl)
                .queryParam("firstName", misbehavingCustomer).toUriString();

        log.debug("calling customers /bootCustomer");
        return template.postForObject(urlTemplate, request, CustomerRecord.class);
    }

    public OrderRecord submitOrder(OrderRecord order){
        if(ordersHost == null || orderSubmitUrl == null) throw new RuntimeException("failed to load environment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(ordersHost + orderSubmitUrl)
                .queryParam("firstName", order.firstName())
                .queryParam("dish", order.dish())
                .queryParam("tableNumber", order.tableNumber())
                .queryParam("bill", order.bill()).toUriString();

        log.debug("calling orders /insertOrder");
        return template.postForObject(urlTemplate, request, OrderRecord.class);
    }

    public List<TableRecord> getAllTables() throws EntityNotFoundException {
        if(tablesHost == null || tableGetAllUrl == null) throw new RuntimeException("failed to load environment");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(tablesHost + tableGetAllUrl).toUriString();

        log.debug("calling tables /getAllTables");
        TableRecord[] tableRecordList = template.getForObject(urlTemplate, TableRecord[].class);

        if(tableRecordList != null) return new ArrayList<>(Arrays.asList(tableRecordList));
        else throw new EntityNotFoundException("no tables found");
    }

    public OrderRecord serveOrder(String firstName, int tableNumber) {
        if(tablesHost == null || tableGetAllUrl == null) throw new RuntimeException("failed to load environment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(ordersHost + serveOrder)
                .queryParam("firstName", firstName)
                .queryParam("tableNumber", tableNumber).toUriString();

        log.debug("calling orders /serveOrder");
        return template.postForObject(urlTemplate, request, OrderRecord.class);
    }
}
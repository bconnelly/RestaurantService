package com.fullstack.restaurantservice.DomainLogic;

import com.fullstack.restaurantservice.DataEntities.*;
import com.fullstack.restaurantservice.RestDataRetrieval.RestFetcher;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class RestaurantLogic {

    private final RestFetcher restFetcher;

    @Autowired
    public RestaurantLogic(RestFetcher restFetcher){
        this.restFetcher = restFetcher;
    }

    public CustomerRecord seatCustomer(CustomerRecord customer) throws EntityNotFoundException {
        List<TableRecord> openTables = getOpenTables();
        if(openTables.isEmpty()) {
            throw new EntityNotFoundException("no empty tables");
        }
        log.info("Seating customer: {}", customer.firstName());
        return restFetcher.seatCustomer(customer, openTables.get(0).tableNumber());
    }

    public List<CustomerRecord> seatGroup(List<CustomerRecord> customers) throws EntityNotFoundException {
        List<TableRecord> openTables = getOpenTables();
        if(openTables.isEmpty()) {
            throw new EntityNotFoundException("no empty tables");
        }

        int groupSize = customers.size();
        TableRecord suitableTable = openTables.stream()
                .filter(table -> table.capacity() >= groupSize)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("no suitable table for group size " + groupSize));

        //rebuild records with table number
        List<CustomerRecord> newCustomers = new ArrayList<>();
        for(CustomerRecord customer: customers){
            CustomerRecord newCustomer = CustomerRecord.builder()
                    .firstName(customer.firstName())
                    .address(customer.address())
                    .cash(customer.cash())
                    .tableNumber(suitableTable.tableNumber()).build();
            newCustomers.add(newCustomer);
        }

        log.info("Seating group of {} customers at table {}", groupSize, suitableTable.tableNumber());
        return restFetcher.seatGroup(newCustomers);
    }

    public List<TableRecord> getOpenTables() throws EntityNotFoundException {
        List<TableRecord> allTables = restFetcher.getAllTables();
        List<CustomerRecord> allCustomers = restFetcher.getAllCustomers();

        if(allTables.isEmpty()) {
            log.error("No tables present in restaurant");
            throw new EntityNotFoundException("No tables present in restaurant");
        }

        for(CustomerRecord customer:allCustomers){
            allTables.removeIf(n -> Objects.equals(n.tableNumber(), customer.tableNumber()));
        }
        return allTables;
    }

    public OrderRecord submitOrder(OrderRecord order) throws EntityNotFoundException, RuntimeException {
        CustomerRecord customer = restFetcher.getCustomerByName(order.firstName());
        if(customer.cash() < order.bill()) {
            log.error("Insufficient funds for customer: {}", order.firstName());
            throw new RuntimeException("customer has insufficient funds");
        }
        log.info("Order submitted by customer: {} for dish: {}", order.firstName(), order.dish());
        return restFetcher.submitOrder(order);
    }

    public CustomerRecord bootCustomer(String firstName) throws EntityNotFoundException {
        return restFetcher.bootCustomer(firstName);
    }

    public OrderRecord serveOrder(String firstName, int tableNumber) {
        log.info("Serving order for customer: {} at table: {}", firstName, tableNumber);
        return restFetcher.serveOrder(firstName, tableNumber);
    }
}
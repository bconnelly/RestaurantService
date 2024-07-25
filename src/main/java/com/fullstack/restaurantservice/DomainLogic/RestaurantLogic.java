package com.fullstack.restaurantservice.DomainLogic;

import com.fullstack.restaurantservice.DataEntities.*;
import com.fullstack.restaurantservice.RestDataRetrieval.RestFetcher;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;

@Slf4j
@Service
public class RestaurantLogic {

    public RestaurantLogic(RestFetcher restFetcher){
        this.restFetcher = restFetcher;
    }

    private RestFetcher restFetcher;

    public CustomerRecord seatCustomer(String firstName, String address, Float cash) throws EntityNotFoundException {
        List<TableRecord> openTables = getOpenTables();
        if(openTables.isEmpty()) throw new EntityNotFoundException("no empty tables");
//        HttpServletRequest
        return restFetcher.seatCustomer(firstName, address, cash, openTables.get(0).tableNumber());
    }

    public List<TableRecord> getOpenTables() throws EntityNotFoundException {
        List<TableRecord> allTables = restFetcher.getAllTables();
        List<CustomerRecord> allCustomers = restFetcher.getAllCustomers();

        for(CustomerRecord customer:allCustomers){
            allTables.removeIf(n -> Objects.equals(n.tableNumber(), customer.tableNumber()));
        }
        return allTables;
    }

    public OrderRecord submitOrder(String firstName, String dish, Integer tableNumber, Float bill) throws EntityNotFoundException, RuntimeException {
        CustomerRecord customer = restFetcher.getCustomerByName(firstName);
        if(customer.cash() < bill) throw new RuntimeException("customer has insufficient funds");
        return restFetcher.submitOrder(firstName, dish, tableNumber, bill);
    }

    public CustomerRecord bootCustomer(String firstName) throws EntityNotFoundException {
        return restFetcher.bootCustomer(firstName);
    }

    public OrderRecord serveOrder(String firstName, int tableNumber) {
        return restFetcher.serveOrder(firstName, tableNumber);
    }
}
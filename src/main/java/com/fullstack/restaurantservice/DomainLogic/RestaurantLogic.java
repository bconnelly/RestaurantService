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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//TODO: handle exceptions thrown in threads that don't have void return type
@Slf4j
@Service
public class RestaurantLogic {

    public RestaurantLogic(RestFetcher restFetcher){
        this.restFetcher = restFetcher;
    }

    private RestFetcher restFetcher;

    public CompletableFuture<CustomerRecord> seatCustomer(String firstName, String address, Float cash) throws EntityNotFoundException, ExecutionException, InterruptedException {
        List<TableRecord> openTables = getOpenTables().get();
        if(openTables.isEmpty()) throw new EntityNotFoundException("no empty tables");

        return restFetcher.seatCustomer(firstName, address, cash, openTables.get(0).tableNumber());
    }

    public CompletableFuture<List<TableRecord>> getOpenTables() throws EntityNotFoundException, ExecutionException, InterruptedException {
        CompletableFuture<List<CustomerRecord>> allCustomersFuture = restFetcher.getAllCustomers();
        CompletableFuture<List<TableRecord>> allTablesFuture = restFetcher.getAllTables();
        CompletableFuture.allOf(allTablesFuture, allCustomersFuture).join();

        List<TableRecord> allTables = allTablesFuture.get();
        List<CustomerRecord> allCustomers = allCustomersFuture.get();

        if(allTables.isEmpty()) throw new RuntimeException("no tables");

        for(CustomerRecord customer:allCustomers){
            allTables.removeIf(n -> Objects.equals(n.tableNumber(), customer.tableNumber()));
        }
        return CompletableFuture.completedFuture(allTables);
    }

    public CompletableFuture<OrderRecord> submitOrder(String firstName, String dish, Integer tableNumber, Float bill) throws EntityNotFoundException, RuntimeException, ExecutionException, InterruptedException {
        CustomerRecord customer = restFetcher.getCustomerByName(firstName).get();
        if(customer.cash() < bill) throw new RuntimeException("customer has insufficient funds");
        return restFetcher.submitOrder(firstName, dish, tableNumber, bill);
    }

    public CompletableFuture<CustomerRecord> bootCustomer(String firstName) throws EntityNotFoundException, ExecutionException, InterruptedException {
        CompletableFuture<CustomerRecord> bootedCustomer = restFetcher.bootCustomer(firstName);
        return CompletableFuture.completedFuture(bootedCustomer.get());

    }
}
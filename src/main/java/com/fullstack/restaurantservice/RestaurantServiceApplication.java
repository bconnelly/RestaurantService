package com.fullstack.restaurantservice;

import com.fullstack.restaurantservice.DataEntities.CustomerRecord;
import com.fullstack.restaurantservice.DataEntities.OrderRecord;
import com.fullstack.restaurantservice.DataEntities.TableRecord;
import com.fullstack.restaurantservice.DomainLogic.RestaurantLogic;
import com.fullstack.restaurantservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@SpringBootApplication
public class RestaurantServiceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RestaurantServiceApplication.class);
    }

    @Autowired
    RestaurantLogic restaurantLogic;

    @GetMapping("/")
    public String defaultLandingPage(){
        log.debug("default landing page requested");
        return "default landing page";
    }

    @PostMapping("/customer")
    public CustomerRecord seatCustomer(@RequestBody CustomerRecord customer) throws EntityNotFoundException {
        log.debug("seatCustomer requested");
        return restaurantLogic.seatCustomer(customer);
    }

    @DeleteMapping("/customer")
    public void bootCustomer(@RequestParam("firstName") String firstName) throws EntityNotFoundException {
        log.debug("bootCustomer requested");
        restaurantLogic.bootCustomer(firstName);
    }

    @PostMapping("/customer/group")
    public List<CustomerRecord> seatGroup(@RequestBody List<CustomerRecord> customers) throws EntityNotFoundException {
        log.debug("seatGroup requested");
        return restaurantLogic.seatGroup(customers);
    }

    @GetMapping("/table/open")
    public List<TableRecord> getOpenTables() throws Exception {
        log.debug("getOpenTables requested");
        return restaurantLogic.getOpenTables();
    }

    @PostMapping("/order/submit")
    public OrderRecord submitOrder(@RequestBody OrderRecord order) throws EntityNotFoundException {
        log.debug("submitOrder requested");
        return restaurantLogic.submitOrder(order);
    }

    @PostMapping("/order/serve")
    public void serveOrder(@RequestParam String firstName, int tableNumber){
        log.debug("serveOrder requested");
        restaurantLogic.serveOrder(firstName, tableNumber);
    }

}
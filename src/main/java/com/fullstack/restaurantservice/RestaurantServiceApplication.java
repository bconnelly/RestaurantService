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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
@SpringBootApplication(scanBasePackages = "com.fullstack.restaurantservice")
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/")
    public ResponseEntity<String> defaultLandingPage(){
        log.debug("default landing page requested");
        return ResponseEntity.status(HttpStatus.OK).body("default landing page");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/seatCustomer")
    public CustomerRecord seatCustomer(@RequestParam("firstName") String firstName,
                                                       @RequestParam("address") String address,
                                                       @RequestParam("cash") Float cash) throws EntityNotFoundException {
        log.debug("seatCustomer requested");
        return restaurantLogic.seatCustomer(firstName, address, cash);
//        return "customer was probably seated!";
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getOpenTables")
    public List<TableRecord> getOpenTables() throws EntityNotFoundException {
        log.debug("getOpenTables requested");
        return restaurantLogic.getOpenTables();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/submitOrder")
    public OrderRecord submitOrder(@RequestParam("firstName")String firstName,
                                                   @RequestParam("dish")String dish,
                                                   @RequestParam("tableNumber")Integer tableNumber,
                                                   @RequestParam("bill")Float bill) throws EntityNotFoundException {
        log.debug("submitOrder requested");
        return restaurantLogic.submitOrder(firstName, dish, tableNumber, bill);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/bootCustomer")
    public CustomerRecord bootCustomer(@RequestParam("firstName") String firstName) throws EntityNotFoundException {
        log.debug("bootCustomer requested");
        return restaurantLogic.bootCustomer(firstName);
    }
}
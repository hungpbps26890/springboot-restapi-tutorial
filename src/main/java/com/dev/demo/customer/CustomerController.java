package com.dev.demo.customer;

import com.dev.demo.exeption.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    private final CustomerMapper customerMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(customerMapper::toCustomerDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        Customer customerToCreate = customerMapper.toCustomer(createCustomerRequest);

        Customer createdCustomer = customerService.createCustomer(customerToCreate);

        return new ResponseEntity<>(customerMapper.toCustomerDto(createdCustomer), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable("id") Long id) {
        Customer foundCustomer = customerService.getCustomerById(id);

        return new ResponseEntity<>(customerMapper.toCustomerDto(foundCustomer), HttpStatus.OK);
    }
}

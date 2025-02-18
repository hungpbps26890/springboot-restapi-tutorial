package com.dev.demo.customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Customer createCustomer(Customer customerToCreate);
}

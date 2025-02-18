package com.dev.demo.customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Customer createCustomer(Customer customerToCreate);

    Customer getCustomerById(Long id);

    Customer fullUpdateCustomer(Long id, Customer customerToUpdate);

    Customer partialUpdate(Long id, Customer customerToUpdate);
}

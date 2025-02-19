package com.dev.demo;

import com.dev.demo.customer.CreateCustomerRequest;
import com.dev.demo.customer.Customer;
import com.dev.demo.customer.CustomerDto;
import com.dev.demo.customer.UpdateCustomerRequest;

public class TestDataUtil {

    public static Customer testCustomer() {
        return Customer.builder()
                .id(1L)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();
    }

    public static Customer testCustomerB() {
        return Customer.builder()
                .id(2L)
                .name("Bob")
                .email("bob@gmail.com")
                .address("US")
                .build();
    }

    public static CustomerDto testCustomerDto() {
        return CustomerDto.builder()
                .id(1L)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();
    }

    public static CreateCustomerRequest testCreateCustomerRequest() {
        return CreateCustomerRequest.builder()
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();
    }

    public static CreateCustomerRequest testCreateCustomerRequestB() {
        return CreateCustomerRequest.builder()
                .name("Bob")
                .email("bob@gmail.com")
                .address("US")
                .build();
    }

    public static Customer testUpdatedCustomer() {
        return Customer.builder()
                .id(1L)
                .name("Alice Trump")
                .email("alicetrump@gmail.com")
                .address("UK")
                .build();
    }

    public static UpdateCustomerRequest testUpdateCustomerRequest() {
        return UpdateCustomerRequest.builder()
                .name("Alice Trump")
                .email("alicetrump@gmail.com")
                .address("UK")
                .build();
    }
}

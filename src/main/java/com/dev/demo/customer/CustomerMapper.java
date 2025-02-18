package com.dev.demo.customer;

import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toCustomerDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();
    }

    public Customer toCustomer(CreateCustomerRequest createCustomerRequest) {
        return Customer.builder()
                .name(createCustomerRequest.getName())
                .email(createCustomerRequest.getEmail())
                .address(createCustomerRequest.getAddress())
                .build();
    }
}

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

    public Customer toCustomerToCreate(CreateCustomerRequest createCustomerRequest) {
        return Customer.builder()
                .name(createCustomerRequest.getName())
                .email(createCustomerRequest.getEmail())
                .address(createCustomerRequest.getAddress())
                .build();
    }

    public Customer toCustomerToUpdate(UpdateCustomerRequest updateCustomerRequest) {
        return Customer.builder()
                .name(updateCustomerRequest.getName())
                .email(updateCustomerRequest.getEmail())
                .address(updateCustomerRequest.getAddress())
                .build();
    }
}

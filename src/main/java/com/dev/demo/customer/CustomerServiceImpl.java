package com.dev.demo.customer;

import com.dev.demo.exeption.CustomerNotFoundException;
import com.dev.demo.exeption.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer createCustomer(Customer customerToCreate) {
        String email = customerToCreate.getEmail();
        if (customerRepository.existsByEmail(email))
            throw new EmailAlreadyExistsException("Email " + email + " already exists");

        return customerRepository.save(customerToCreate);
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + id));
    }

    @Override
    public Customer fullUpdateCustomer(Long id, Customer customerToUpdate) {
        Customer existingCustomer = getCustomerById(id);

        String email = customerToUpdate.getEmail();
        if (customerRepository.existsByEmail(email) && !existingCustomer.getEmail().equals(email)) {
            throw new EmailAlreadyExistsException("Email " + email + " already exists");
        }

        existingCustomer.setName(customerToUpdate.getName());
        existingCustomer.setEmail(email);
        existingCustomer.setAddress(customerToUpdate.getAddress());

        return customerRepository.save(existingCustomer);
    }

    @Override
    public Customer partialUpdate(Long id, Customer customerToUpdate) {
        Customer existingCustomer = getCustomerById(id);

        String email = customerToUpdate.getEmail();
        if (customerRepository.existsByEmail(email) && !existingCustomer.getEmail().equals(email)) {
            throw new EmailAlreadyExistsException("Email " + email + " already exists");
        }

        Optional.ofNullable(customerToUpdate.getName()).ifPresent(existingCustomer::setName);
        Optional.ofNullable(customerToUpdate.getEmail()).ifPresent(existingCustomer::setEmail);
        Optional.ofNullable(customerToUpdate.getAddress()).ifPresent(existingCustomer::setAddress);

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer existingCustomer = getCustomerById(id);

        customerRepository.delete(existingCustomer);
    }
}

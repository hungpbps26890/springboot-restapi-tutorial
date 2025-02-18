package com.dev.demo.customer;

import com.dev.demo.exception.CustomerNotFoundException;
import com.dev.demo.exception.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Autowired
    private CustomerServiceImpl underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void TestThat_getAllCustomers_ShouldGetAllCustomers() {
        //when
        underTest.getAllCustomers();

        //then
        verify(customerRepository).findAll();
    }

    @Test
    void TestThat_createCustomer_ShouldCreateCustomer() {
        //given
        Customer customer = Customer.builder()
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        //when
        underTest.createCustomer(customer);

        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void TestThat_createCustomer_ShouldThrowEmailAlreadyExistsException_WhenEmailAlreadyExists() {
        //given
        String email = "alice@gmail.com";

        Customer customer = Customer.builder()
                .name("Alice")
                .email(email)
                .address("US")
                .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.createCustomer(customer))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email " + email + " already exists");
    }

    @Test
    void TestThat_getCustomerById_ShouldReturnFoundCustomer() {
        //given
        long id = 1L;

        Customer customer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        //when
        Customer result = underTest.getCustomerById(id);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(customer.getName());
        assertThat(result.getEmail()).isEqualTo(customer.getEmail());
        assertThat(result.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void TestThat_getCustomerById_ShouldThrowCustomerNotFoundException_WhenCustomerDoesNotExists() {
        //given
        long id = 1L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with id " + id);

        verify(customerRepository, never()).save(any());
    }

    @Test
    void TestThat_fullUpdateCustomer_ShouldFullUpdateCustomer() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);

        //when
        underTest.fullUpdateCustomer(id, customerToUpdate);

        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }

    @Test
    void TestThat_fullUpdateCustomer_ShouldCustomerNotFoundException_WhenCustomerIdDoesNotExist() {
        //given
        long id = 1L;

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> underTest.fullUpdateCustomer(id, customerToUpdate))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with id " + id);
    }

    @Test
    void TestThat_fullUpdateCustomer_EmailAlreadyExistsException_WhenNewEmailAlreadyExists() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        //when

        //then
        assertThatThrownBy(() -> underTest.fullUpdateCustomer(id, customerToUpdate))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email " + newEmail + " already exists");
    }

    @Test
    void TestThat_partialUpdate_ShouldFullUpdateCustomer() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);

        //when
        underTest.partialUpdate(id, customerToUpdate);

        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }

    @Test
    void TestThat_partialUpdate_ShouldOnlyUpdateCustomerName() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        //when
        underTest.partialUpdate(id, customerToUpdate);

        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(existingCustomer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(existingCustomer.getAddress());
    }

    @Test
    void TestThat_partialUpdate_ShouldOnlyUpdateCustomerEmail() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .email(newEmail)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);

        //when
        underTest.partialUpdate(id, customerToUpdate);

        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(existingCustomer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(existingCustomer.getAddress());
    }

    @Test
    void TestThat_partialUpdateCustomer_EmailAlreadyExistsException_WhenNewEmailAlreadyExists() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        String newName = "Alice Trump";
        String newEmail = "alicetrump@gmail.com";
        String newAddress = "UK";

        Customer customerToUpdate = Customer.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        //when

        //then
        assertThatThrownBy(() -> underTest.partialUpdate(id, customerToUpdate))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email " + newEmail + " already exists");
    }

    @Test
    void TestThat_deleteCustomer_ShouldDeleteCustomer() {
        //given
        long id = 1L;

        Customer existingCustomer = Customer.builder()
                .id(id)
                .name("Alice")
                .email("alice@gmail.com")
                .address("US")
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        //when
        underTest.deleteCustomer(id);

        //then
        verify(customerRepository).delete(existingCustomer);
    }

    @Test
    void TestThat_deleteCustomer_ShouldThrowCustomerNotFoundException_WhenCustomerIdDoesNotExist() {
        //given
        long id = 1L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with id " + id);

        verify(customerRepository, never()).delete(any());
    }
}
package com.dev.demo.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    private String existingEmail;

    @BeforeEach
    void setUp() {
        existingEmail = "alice@gmail.com";

        Customer customer = Customer.builder()
                .name("Alice")
                .email(existingEmail)
                .address("US")
                .build();

        underTest.save(customer);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void TestThat_existsByEmail_ReturnsTrue_WhenCustomerEmailExists() {
        //given

        //when
        boolean result = underTest.existsByEmail(existingEmail);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void TestThat_existsByEmail_ReturnsFalse_WhenCustomerEmailDoesNotExist() {
        //given
        String email = "alex@gmail.com";

        //when
        boolean result = underTest.existsByEmail(email);

        //then
        assertThat(result).isFalse();
    }
}
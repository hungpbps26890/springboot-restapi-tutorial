package com.dev.demo.customer;

import com.dev.demo.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerControllerIntegrationTest {

    private final String CUSTOMER_BASED_URL = "/api/v1/customers";

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomerControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper, CustomerRepository customerRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @Order(1)
    void TestThat_getAllCustomers_ShouldReturnHttp200OKAndAnEmptyList_WhenThereIsNoCustomerInTheDB() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(CUSTOMER_BASED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print());
    }

    @Test
    @Order(2)
    void TestThat_createCustomer_ShouldReturnHttp201CreatedAndCreatedCustomerDto() throws Exception {
        //given
        CreateCustomerRequest createCustomerRequest = TestDataUtil.testCreateCustomerRequest();

        CustomerDto expectedCreatedCustomerDto = TestDataUtil.testCustomerDto();

        String content = objectMapper.writeValueAsString(createCustomerRequest);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(CUSTOMER_BASED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(expectedCreatedCustomerDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedCreatedCustomerDto.getEmail()))
                .andExpect(jsonPath("$.address").value(expectedCreatedCustomerDto.getAddress()))
                .andDo(print());
    }

    @Test
    @Order(3)
    void TestThat_getAllCustomers_ShouldReturnHttp200OKAndListOfCustomerDto() throws Exception {
        //given
        CustomerDto expectedCustomerDto = TestDataUtil.testCustomerDto();

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(CUSTOMER_BASED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedCustomerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(expectedCustomerDto.getName()))
                .andExpect(jsonPath("$[0].email").value(expectedCustomerDto.getEmail()))
                .andExpect(jsonPath("$[0].address").value(expectedCustomerDto.getAddress()))
                .andDo(print());
    }

    @Test
    @Order(4)
    void TestThat_createCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        CreateCustomerRequest createCustomerRequest = TestDataUtil.testCreateCustomerRequest();

        String content = objectMapper.writeValueAsString(createCustomerRequest);

        String message = "Email " + createCustomerRequest.getEmail() + " already exists";

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(CUSTOMER_BASED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(5)
    void TestThat_getCustomerById_ShouldReturnHttp200OKAndFoundCustomerDto() throws Exception {
        //given
        Customer customer = TestDataUtil.testCustomer();

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(CUSTOMER_BASED_URL + "/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.email").value(customer.getEmail()))
                .andExpect(jsonPath("$.address").value(customer.getAddress()))
                .andDo(print());
    }

    @Test
    @Order(6)
    void TestThat_getCustomerById_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 0L;

        String message = "Customer not found with id " + id;


        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(7)
    void TestThat_fullUpdateCustomer_ShouldReturnHttp200OKAndUpdatedCustomerDto() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedCustomer.getId()))
                .andExpect(jsonPath("$.name").value(updatedCustomer.getName()))
                .andExpect(jsonPath("$.email").value(updatedCustomer.getEmail()))
                .andExpect(jsonPath("$.address").value(updatedCustomer.getAddress()))
                .andDo(print());
    }

    @Test
    @Order(8)
    void TestThat_fullUpdateCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 0L;

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Customer not found with id " + id;

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(9)
    void TestThat_fullUpdateCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        CreateCustomerRequest createCustomerRequestB = TestDataUtil.testCreateCustomerRequestB();
        String createCustomerRequestBJSON = objectMapper.writeValueAsString(createCustomerRequestB);
        mockMvc.perform(
                MockMvcRequestBuilders.post(CUSTOMER_BASED_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(createCustomerRequestBJSON)
        );

        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        updateCustomerRequest.setEmail(createCustomerRequestB.getEmail());

        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String email = updateCustomerRequest.getEmail();

        String message = "Email " + email + " already exists";

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(10)
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndFullUpdatedCustomerDto() throws Exception {
        //given
        Long id = 1L;

        String newName = "Alice Biden";
        String newEmail = "alicebiden@gmail.com";
        String newAddress = "US";

        UpdateCustomerRequest updateCustomerRequest = UpdateCustomerRequest.builder()
                .name(newName)
                .email(newEmail)
                .address(newAddress)
                .build();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.address").value(newAddress))
                .andDo(print());
    }

    @Test
    @Order(11)
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndOnlyUpdatedCustomerName() throws Exception {
        //given
        Long id = 1L;

        String newName = "Alice Trump";
        String oldEmail = "alicebiden@gmail.com";
        String oldAddress = "US";

        UpdateCustomerRequest updateCustomerRequest = UpdateCustomerRequest.builder()
                .name(newName)
                .build();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.email").value(oldEmail))
                .andExpect(jsonPath("$.address").value(oldAddress))
                .andDo(print());
    }

    @Test
    @Order(12)
    void TestThat_partialUpdateCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 0L;

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Customer not found with id " + id;

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(13)
    void TestThat_partialUpdateCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        Customer existingCustomer = TestDataUtil.testCustomerB();
        String email = existingCustomer.getEmail();

        Long id = 1L;

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        updateCustomerRequest.setEmail(email);
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Email " + email + " already exists";

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }

    @Test
    @Order(14)
    void TestThat_deleteCustomer_ShouldDeleteCustomerAndReturnHttp204NoContent() throws Exception {
        //given
        Long id = 1L;

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.delete(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @Order(15)
    void TestThat_deleteCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 1L;

        String message = "Customer not found with id " + id;

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.delete(CUSTOMER_BASED_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(message))
                .andDo(print());
    }
}

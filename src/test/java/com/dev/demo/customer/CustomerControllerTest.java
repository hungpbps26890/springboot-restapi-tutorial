package com.dev.demo.customer;

import com.dev.demo.TestDataUtil;
import com.dev.demo.exception.CustomerNotFoundException;
import com.dev.demo.exception.EmailAlreadyExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    private final String CUSTOMER_BASED_URL = "/api/v1/customers";

    @MockitoBean
    private final CustomerService customerService;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomerControllerTest(CustomerService customerService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.customerService = customerService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void TestThat_getAllCustomers_ShouldReturnHttp200OKAndListOfCustomerDto() throws Exception {
        //given
        Customer customer = TestDataUtil.testCustomer();

        when(customerService.getAllCustomers()).thenReturn(List.of(customer));

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
    void TestThat_createCustomer_ShouldReturnHttp201CreatedAndCreatedCustomerDto() throws Exception {
        //given
        CreateCustomerRequest createCustomerRequest = TestDataUtil.testCreateCustomerRequest();

        Customer createdCustomer = TestDataUtil.testCustomer();

        CustomerDto expectedCreatedCustomerDto = TestDataUtil.testCustomerDto();

        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(createdCustomer);

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
                .andExpect(jsonPath("$.id").value(expectedCreatedCustomerDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedCreatedCustomerDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedCreatedCustomerDto.getEmail()))
                .andExpect(jsonPath("$.address").value(expectedCreatedCustomerDto.getAddress()))
                .andDo(print());
    }

    @Test
    void TestThat_createCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        CreateCustomerRequest createCustomerRequest = TestDataUtil.testCreateCustomerRequest();

        String content = objectMapper.writeValueAsString(createCustomerRequest);

        String message = "Email " + createCustomerRequest.getEmail() + " already exists";

        when(customerService.createCustomer(any(Customer.class)))
                .thenThrow(new EmailAlreadyExistsException(message));

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
    void TestThat_getCustomerById_ShouldReturnHttp200OKAndFoundCustomerDto() throws Exception {
        //given
        Customer customer = TestDataUtil.testCustomer();
        Long id = customer.getId();

        when(customerService.getCustomerById(anyLong())).thenReturn(customer);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(CUSTOMER_BASED_URL + "/" + id)
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
    void TestThat_getCustomerById_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Customer customer = TestDataUtil.testCustomer();
        Long id = customer.getId();

        String message = "Customer not found with id " + id;

        when(customerService.getCustomerById(anyLong()))
                .thenThrow(new CustomerNotFoundException(message));

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
    void TestThat_fullUpdateCustomer_ShouldReturnHttp200OKAndUpdatedCustomerDto() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        when(customerService.fullUpdateCustomer(anyLong(), any(Customer.class)))
                .thenReturn(updatedCustomer);

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
    void TestThat_fullUpdateCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 0L;

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Customer not found with id " + id;

        when(customerService.fullUpdateCustomer(anyLong(), any(Customer.class)))
                .thenThrow(new CustomerNotFoundException(message));

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
    void TestThat_fullUpdateCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String email = updateCustomerRequest.getEmail();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Email " + email + " already exists";

        when(customerService.fullUpdateCustomer(anyLong(), any(Customer.class)))
                .thenThrow(new EmailAlreadyExistsException(message));

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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndFullUpdatedCustomerDto() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenReturn(updatedCustomer);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndOnlyUpdateCustomerName() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testCustomer();
        Long id = updatedCustomer.getId();

        String newName = "Alice Trump";

        UpdateCustomerRequest updateCustomerRequest = UpdateCustomerRequest.builder()
                .name(newName)
                .build();

        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        updatedCustomer.setName(newName);

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenReturn(updatedCustomer);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndOnlyUpdateCustomerEmail() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testCustomer();
        Long id = updatedCustomer.getId();

        String newEmail = "alicetrump@gmail.com";

        UpdateCustomerRequest updateCustomerRequest = UpdateCustomerRequest.builder()
                .email(newEmail)
                .build();

        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        updatedCustomer.setEmail(newEmail);

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenReturn(updatedCustomer);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp200OKAndOnlyUpdateCustomerAddress() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testCustomer();
        Long id = updatedCustomer.getId();

        String newAddress = "UK";

        UpdateCustomerRequest updateCustomerRequest = UpdateCustomerRequest.builder()
                .address(newAddress)
                .build();

        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        updatedCustomer.setAddress(newAddress);

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenReturn(updatedCustomer);

        //when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.patch(CUSTOMER_BASED_URL + "/" + id)
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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 0L;

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Customer not found with id " + id;

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenThrow(new CustomerNotFoundException(message));

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
    void TestThat_partialUpdateCustomer_ShouldReturnHttp400BadRequest_WhenEmailAlreadyExists() throws Exception {
        //given
        Customer updatedCustomer = TestDataUtil.testUpdatedCustomer();
        Long id = updatedCustomer.getId();

        UpdateCustomerRequest updateCustomerRequest = TestDataUtil.testUpdateCustomerRequest();
        String email = updateCustomerRequest.getEmail();
        String content = objectMapper.writeValueAsString(updateCustomerRequest);

        String message = "Email " + email + " already exists";

        when(customerService.partialUpdate(anyLong(), any(Customer.class)))
                .thenThrow(new EmailAlreadyExistsException(message));

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
    void TestThat_deleteCustomer_ShouldDeleteCustomerAndReturnHttp204NoContent() throws Exception {
        //given
        Long id = 1L;

        doNothing().when(customerService).deleteCustomer(anyLong());

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
    void TestThat_deleteCustomer_ShouldReturnHttp404NotFound_WhenCustomerNotFoundWithId() throws Exception {
        //given
        Long id = 1L;

        String message = "Customer not found with id " + id;

        doThrow(new CustomerNotFoundException(message))
                .when(customerService)
                .deleteCustomer(anyLong());

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

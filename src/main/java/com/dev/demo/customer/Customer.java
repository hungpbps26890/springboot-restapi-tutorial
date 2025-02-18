package com.dev.demo.customer;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String address;
}

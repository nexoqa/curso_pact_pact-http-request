package com.nexoqa.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    private String name;
    private String lastName;
    private String address;
    private String email;
    private Integer age;
    private Integer phoneNumber;

}

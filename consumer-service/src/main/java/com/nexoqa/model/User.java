package com.nexoqa.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    private Integer id;
    private String name;
    private String lastName;
    private String address;
    private String email;
    private Integer age;
    private Integer phoneNumber;

}

package com.nexoqa.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Client {

    private User user;
    private BloodType bloodType;
    // private boolean activated;

}

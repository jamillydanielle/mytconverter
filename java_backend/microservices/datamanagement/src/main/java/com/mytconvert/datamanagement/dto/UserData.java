package com.mytconvert.datamanagement.dto;

import com.mytconvert.datamanagement.entity.user.UserType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    private long id;
    private String name;
    private String email;
    private UserType type;
}

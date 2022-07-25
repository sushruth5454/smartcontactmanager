package com.smart.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name field is Required")
    @Size(min = 3, max = 10, message = "User Name Should be of lenght 3 to 10")
    private String name;

    @Column(unique = true)
    @Email(message = "Enter a Valid Email address")
    private String email;
    private String password;
    private String role;
    private String imageUrl;
    private boolean enabled;
    @Column(length = 500)
    private String about;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact>contacts=new ArrayList<>();
}

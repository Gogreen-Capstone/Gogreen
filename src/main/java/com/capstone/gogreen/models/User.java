package com.capstone.gogreen.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message ="*Required")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message ="*Required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message ="*Required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "is_employee")
    private boolean isEmployee;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Job> jobList;

    @OneToOne
    private Location location;

    public User() {
    }

    public User(long id, @NotBlank(message = "*Required") String username, @NotBlank(message = "*Required") String email, @NotBlank(message ="*Required") @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters") String password, boolean isAdmin, boolean isEmployee) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isEmployee = isEmployee;
    }

    public User(User copy) {
        id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        email = copy.email;
        username = copy.username;
        password = copy.password;
//        isAdmin = copy.isAdmin;
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsEmployee() {
        return isEmployee;
    }

    public void setEmployee(boolean employee) {
        isEmployee = employee;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }
}
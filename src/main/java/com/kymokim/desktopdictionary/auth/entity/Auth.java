package com.kymokim.desktopdictionary.auth.entity;


import com.kymokim.desktopdictionary.auth.security.role.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(name="auth")
@Entity
@Getter
@NoArgsConstructor
@Data
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "salt")
    private String salt;

    @Column(name = "tel")
    private String tel = null;

    @Column(name = "addr")
    private String addr = null;

    @Column(name = "postcode")
    private String postcode = null;

    @Column(name = "bankAccount")
    private String bankAccount = null;

    @Column(name = "bankName")
    private String bankName = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Builder
    public Auth(String email, String password, String name, String salt, Role role){
        this.email = email;
        this.password = password;
        this.name = name;
        this.salt = salt;
        this.role = role;
    }

    public void update(String password, String name, String salt) {
        this.password = password;
        this.name = name;
        this.salt = salt;
    }

    public void addTradeInfo(String tel, String addr, String postcode){
        this.tel = tel;
        this.addr = addr;
        this.postcode = postcode;
    }

    public void addBankInfo(String bankAccount, String bankName){
        this.bankAccount = bankAccount;
        this.bankName = bankName;
    }
}

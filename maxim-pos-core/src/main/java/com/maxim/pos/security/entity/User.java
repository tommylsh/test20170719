package com.maxim.pos.security.entity;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.maxim.entity.AbstractEntity;
import com.maxim.user.Principal;

@Entity
@Table(name = "SECURITY_USER")
public class User extends AbstractEntity implements Principal, Comparable<User> {

    private static final long serialVersionUID = 8722875205943586915L;

    private String userId;

    private String password;

    private String userName;

    private boolean admin;

    private SortedSet<Role> roles = new TreeSet<Role>();

    @Override
    @Id
    @Column(name = "SECURITY_USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }
    
    @Column(name = "USER_ID", length = 50, unique = true, nullable = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonIgnore
    @Column(name = "PASSWORD", length = 256, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "USER_NAME", length = 50, unique = true, nullable = false)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "IS_ADMIN", nullable = false, columnDefinition = "TINYINT", length = 1)
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST , fetch = FetchType.LAZY)
    @JoinTable(name = "SECURITY_USER_ROLES", joinColumns = {
            @JoinColumn(name = "USER_ID", referencedColumnName = "SECURITY_USER_ID") }, inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID", referencedColumnName = "SECURITY_ROLE_ID") })
    @Sort(type = SortType.NATURAL)
    public SortedSet<Role> getRoles() {
        return roles;
    }

    public void setRoles(SortedSet<Role> roles) {
        this.roles = roles;
    }

    @Override
    public int compareTo(User o) {
        return userId.compareTo(o.userId);
    }

}

package com.maxim.pos.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.ClientType;

/**
 * POS CLIENT
 *
 * @author Lotic
 */
@Entity
@Table(name = "POLL_BRANCH_INFO")
public class BranchInfo extends AbstractEntity {

    private static final long serialVersionUID = 3274513161312165055L;
    
    

    private ClientType clientType;
    private String clientHost;
    private Integer clientPort;
    private String clientDB;
    private String user;
    private String password;
    private boolean enable;

    @Override
    @Id
    @Column(name = "POLL_BRANCH_INFO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "CLIENT_TYPE", length = 10)
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    @Column(name = "CLIENT_HOST", length = 200)
    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    @Column(name = "CLIENT_PORT")
    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    @Column(name = "CLIENT_DB", length = 200)
    public String getClientDB() {
        return clientDB;
    }

    public void setClientDB(String clientDB) {
        this.clientDB = clientDB;
    }

    @Column(name = "LOGIN_USER", length = 200)
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name = "LOGIN_PASSWORD", length = 200)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "ENABLE")
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

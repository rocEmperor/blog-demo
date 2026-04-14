package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 昵称（库中沿用 username 列） */
    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "email", length = 128)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @Column(name = "bio", length = 50)
    private String bio;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "failed_login_count", nullable = false)
    private int failedLoginCount = 0;

    @Column(name = "locked_until")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockedUntil;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public Date getLockedUntil() {
        return lockedUntil;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFailedLoginCount(int failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    public void setLockedUntil(Date lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}

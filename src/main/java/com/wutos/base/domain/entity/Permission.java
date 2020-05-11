package com.wutos.base.domain.entity;

import java.time.LocalDateTime;

/**
 * 权限实体
 * @Author: ZouCong
 * @Date: 2018/6/29
 */
public class Permission{
    private Long id;
    private LocalDateTime creationTime;
    private Long creatorUserId;
    private String discriminator;
    private Boolean isGranted = true;
    private String name;
    private Integer tenantId;
    private Integer roleId;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public Long getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(Long creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public Boolean getGranted() {
        return isGranted;
    }

    public void setGranted(Boolean granted) {
        isGranted = granted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        userId = userId;
    }
}

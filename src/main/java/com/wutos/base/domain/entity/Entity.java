package com.wutos.base.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wutos.base.common.protocol.JsonReq;
import com.wutos.base.common.util.ParamsUtil;
import com.wutos.base.common.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Entity {
    /**
     * ID
     */
    private String id;

    /**
     * 删除标记
     */
    @JsonIgnore
    private Integer deleted;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedOn;

    /**
     * 删除者
     */
    @JsonIgnore
    private String deleter;

    /**
     * 删除时间
     */
    @JsonIgnore
    private LocalDateTime deletedOn;

    /**
     * 数据版本，用于乐观并发，非强制。 针对数据一致性要求高的数据，如账户（余额），库存量，序列号等等。
     */
    private Long version;
    /**
     * 租户id
     */
    private Integer tenantId;

    /**
     * tenant是否被改变过
     */
    private Boolean tenantChange = false;

    public String getDeleter() {
        return deleter;
    }

    public void setDeleter(String deleter) {
        this.deleter = deleter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public LocalDateTime getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(LocalDateTime deletedOn) {
        this.deletedOn = deletedOn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getTenantChange() {
        return tenantChange;
    }

    public void setTenantChange(Boolean tenantChange) {
        this.tenantChange = tenantChange;
    }

    /**
     * description:插入数据之前先执行此方法
     *
     * @return
     * @throws @date xiaoyongjun 2018/4/14 9:06
     */
    public void preInsert() {
        if (StringUtils.isBlank(this.id)) {
            setId(UUIDUtils.getUuidCode());
        }
        JsonReq req = ParamsUtil.getJsonReqFromRequest();
        if (!Objects.isNull(req.getUserId())) {
            this.creator = String.valueOf(req.getUserId());
            this.modifier = String.valueOf(req.getUserId());
        }
        this.tenantId = req.getTenantId();
        this.modifiedOn = LocalDateTime.now();
        this.createdOn = LocalDateTime.now();
    }

    /**
     * description:插入数据之前先执行此方法（不生成Id和修改信息）
     */
    public void preInsertCreate() {
        JsonReq req = ParamsUtil.getJsonReqFromRequest();
        if (!Objects.isNull(req.getUserId())) {
            this.creator = req.getUserId().toString();
        }
        this.tenantId = req.getTenantId();
        this.createdOn = LocalDateTime.now();
    }

    /**
     * description:修改数据之前先执行此方法
     *
     * @return
     * @throws @date xiaoyongjun 2018/4/14 9:06
     */
    public void preUpdate() {
        JsonReq req = ParamsUtil.getJsonReqFromRequest();
        if (!Objects.isNull(req.getUserId())) {
            this.modifier = req.getUserId().toString();
        }
        this.tenantId = req.getTenantId();
        this.modifiedOn = LocalDateTime.now();
    }
}

package com.wutos.base.service;

import com.wutos.base.domain.entity.Permission;

import java.util.List;

/**
 * @Author: ZouCong
 * @Date: 2018/6/29
 */
public interface IPermissionService {

    List<Permission> findPrivileges(List<Long> roleIds);
}

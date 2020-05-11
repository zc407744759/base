package com.wutos.base.service.impl;

import com.wutos.base.domain.entity.Permission;
import com.wutos.base.domain.mapper.primary.PermissionMapper;
import com.wutos.base.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: ZouCong
 * @Date: 2018/6/29
 */
@Service("permissionService")
@Transactional
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Value("${applicationName}")
    private String applicationName;

    @Override
    public List<Permission> findPrivileges(List<Long> roleIds) {

        if(roleIds!=null){
           return permissionMapper.selectBatchIds(roleIds,applicationName);
        }else {
            return null;
        }
    }
}

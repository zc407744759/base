package com.wutos.base.domain.mapper.primary;


import com.wutos.base.domain.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ZouCong
 * @Date: 2018/6/29
 */
@Mapper
public interface PermissionMapper {


     List<Permission> selectBatchIds(@Param("list") List<? extends Serializable> idList, @Param("applicationName") String applicationName);
}

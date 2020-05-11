package com.wutos.base.domain.mapper;

import com.github.pagehelper.Page;
import com.wutos.base.common.util.RequestPage;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础Mapper
 * 
 * @author myc
 * @date 2018/4/8.
 */
public interface EntityMapper<T> {

    /**
     * <p>
     * 插入一条记录
     * </p>
     *
     * @param entity
     *            实体对象
     * @return int
     */
    Integer insertOneEntity(T entity);

    /**
     * <p>
     * 插入批量记录
     * </p>
     *
     * @param entityList
     *            实体对象
     * @return int
     */
    void insertManyEntity(List<T> entityList);

    /**
     * <p>
     * 插入批量记录
     * </p>
     *
     * @param map
     *            参数 map 对象
     * @return
     */
    void insertByMap(Map<String, Object> map);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id
     *            主键ID
     * @return int
     */
    Integer deleteById(@Param("id") Serializable id);


    /**
     * <p>
     * 根据 columnMap 条件，删除记录
     * </p>
     *
     * @param columnMap
     *            表字段 map 对象
     * @return int
     */
    Integer deleteByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 逻辑删除，根据对象更方便，删除记录
     * </p>
     * add by xiaoyongjun
     *
     * @param entity
     * @return int
     */
    Integer deleteByEntity(T entity);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList
     *            主键ID列表
     * @return int
     */
    Integer deleteBatchByIds(List<? extends Serializable> idList);


    /**
     * <p>
     * 根据 ID 修改
     * </p>
     *
     * @param entity
     *            实体对象
     * @return int
     */
    Integer updateById(T entity);

    /**
     * <p>
     * 根据 ID 修改
     * </p>
     *
     * @param map
     *
     * @return int
     */
    Integer updateByMap(Map map);

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id
     *            主键ID
     * @return T
     */
    T selectById(@Param("id") Serializable id);

    /**
     * <p>
     * 查询所有
     * </p>
     *
     * @return List<T>
     */
    List<T> selectAll();

    /**
     * <p>
     * 查询所有 带租户id
     * </p>
     *
     * @return List<T>
     */
    List<T> selectAllByTenantId(@Param("tenantId") Integer tenantId);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList
     *            主键ID列表
     * @return List<T>
     */
    List<T> selectBatchByIds(List<? extends Serializable> idList);

    /**
     * <p>
     * 查询（根据 columnMap 条件）
     * </p>
     *
     * @param columnMap
     *            表字段 map 对象
     * @return List<T>
     */
    List<T> selectByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 查询（根据 fields 条件)
     * </p>
     *
     * @param fields
     *            条件字段
     * @return List<T>
     */
    List<T> selectList(Map<String,Object> fields);

    /**
     * description: 分页查询
     *
     * @param requestPage
     *            包括type，keyword的key值
     * @return
     * @date 2018/4/12 12:46
     */
    List<T> getForPage(RequestPage requestPage);

    /**
     * description: 获取所有实例列表可选择
     *
     * @return
     * @date 2018/4/12 12:46
     */
    List<Map<String, Object>> selectAll(@Param("tenantId") Integer tenantId);
}

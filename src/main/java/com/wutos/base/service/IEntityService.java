package com.wutos.base.service;


import com.github.pagehelper.PageInfo;
import com.wutos.base.common.util.RequestPage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础Service
 *
 * @author zc
 * @date 2018/4/8.
 */
public interface IEntityService<T> {

    /**
     * <p>
     * 插入一条记录
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    boolean insertOneEntity(T entity);

    /**
     * <p>
     * 插入批量记录
     * </p>
     *
     * @param entityList 实体对象
     * @return boolean
     */
    void insertManyEntity(List<T> entityList);

    /**
     * <p>
     * 插入批量记录
     * </p>
     *
     * @param map 参数 map 对象
     * @return
     */
    void insertByMap(Map<String, Object> map);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id 主键ID
     * @return boolean
     */
    boolean deleteById(Serializable id);

    boolean delete(T entity);

    /**
     * <p>
     * 根据 columnMap 条件，删除记录
     * </p>
     *
     * @param columnMap 表字段 map 对象
     * @return boolean
     */
    boolean deleteByMap(Map<String, Object> columnMap);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList 主键ID列表
     * @return boolean
     */
    boolean deleteBatchIds(List<? extends Serializable> idList);

    /**
     * <p>
     * 根据 ID 修改
     * </p>
     *
     * @param entity 实体对象
     * @return boolean
     */
    boolean updateById(T entity);


    /**
     * <p>
     * 根据 ID 修改
     * </p>
     *
     * @param map
     * @return boolean
     */
    boolean updateByMap(Map map);

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id 主键ID
     * @return T
     */
    T selectById(Serializable id);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList 主键ID列表
     * @return List<T>
     */
    List<T> selectBatchIds(List<? extends Serializable> idList);

    /**
     * <p>
     * 查询（根据 columnMap 条件）
     * </p>
     *
     * @param columnMap 表字段 map 对象
     * @return List<T>
     */
    List<T> selectByMap(Map<String, Object> columnMap);

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
    List<T> selectAllByTenantId(Integer tenantId);

    List<T> selectList(Map<String,Object> fields);

    /**
     * 分页显示
     * description:
     *
     * @param page "pageNum":第几页,
     *             "pageSize":每页多少行,
     *             "param":{
     *             "type":类型ID,
     *             "keyword":搜索关键字
     *             }
     * @return
     * @date 2018/4/11 20:30
     */
    PageInfo<T> pageList(RequestPage page);
}


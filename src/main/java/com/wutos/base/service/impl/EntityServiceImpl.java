package com.wutos.base.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wutos.base.common.handler.WutosException;
import com.wutos.base.common.protocol.JsonReq;
import com.wutos.base.common.util.ParamsUtil;
import com.wutos.base.common.util.RequestPage;
import com.wutos.base.domain.entity.Entity;
import com.wutos.base.domain.mapper.EntityMapper;
import com.wutos.base.service.IEntityService;
import com.wutos.base.service.dto.RejectedEntityDeleting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zc
 * @date 2020-02-24
 */
public abstract class EntityServiceImpl<M extends EntityMapper<T>,T extends Entity> implements IEntityService<T>{
    @Autowired
    protected M baseMapper;

    /**
     * <p>
     * 判断数据库操作是否成功
     * </p>
     * <p>
     * 注意！！ 该方法为 Integer 判断，不可传入 int 基本类型
     * </p>
     *
     * @param result 数据库操作返回影响条数
     * @return boolean
     */
    protected static boolean retBool(Integer result) {
        return (null == result) ? false : result >= 1;
    }
    /**
     * 对增删改对应的操作人和时间做自动写入
     * @param type
     * @param entity
     */
    private void setUserAndTimeValue(String type,Entity entity) {
        JsonReq req = ParamsUtil.getJsonReq();
        if (!Objects.isNull(req.getUserId())) {
            if("delete".equals(type)){
                entity.setDeleter(req.getUserId().toString());
            }
            if("insert".equals(type)){
                entity.setCreator(req.getUserId().toString());
            }
            if("update".equals(type)){
                entity.setModifier(req.getUserId().toString());
            }
        }
        if("delete".equals(type)){
            entity.setDeletedOn(LocalDateTime.now());
        }
        if("insert".equals(type)){
            entity.setCreatedOn(LocalDateTime.now());
        }
        if("update".equals(type)){
            entity.setModifiedOn(LocalDateTime.now());
        }
    }
    /**
     * 删除数据之前先执行此方法
     *
     * @param entity
     */
    public void preDelete(T entity){
    }

    /**
     *
     * 插入数据前执行此方法
     * @param entity 实体对象
     * @return
     */
    public void preInsert(T entity){};
    public void preInsert(List<T> entityLis){};
    public void preInsert(Map<String, Object> map){};

    /**
     * 插入一条数据
     * @param entity 实体对象
     * @return
     */
    @Override
    public boolean insertOneEntity(T entity) {
        setUserAndTimeValue("insert",entity);
        preInsert(entity);
        return retBool(baseMapper.insertOneEntity(entity));
    }

    /**
     * 插入多条数据
     * @param entityList 实体对象
     */
    @Override
    public void insertManyEntity(List<T> entityList) {
        preInsert(entityList);
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        baseMapper.insertManyEntity(entityList);
    }

    /**
     * 通过map向数据库中插入数据
     * @param map 参数 map 对象
     */
    @Override
    public void insertByMap(Map<String, Object> map) {
        preInsert(map);
        if (CollectionUtils.isEmpty(map) || CollectionUtils.isEmpty((List) map.get("list"))) {
            return;
        }
        baseMapper.insertByMap(map);
    }

    /**
     * 通过主键删除数据 转为了delete(T entity)
     * @param id 主键ID
     * @return
     */
    @Override
    public boolean deleteById(Serializable id) {

        T entity = selectById(id);
        return delete(entity);
    }

    /**
     * 删除记录之前判断有没有依赖关系 并且对操作信息做记录 推荐使用
     * @param entity
     * @return
     */
    @Override
    public boolean delete(T entity) {
        setUserAndTimeValue("delete",entity);
        preDelete(entity);
        if (RejectedEntityDeleting.isValidated()) {
            return retBool(baseMapper.deleteByEntity(entity));
        }
        return false;
    }


    /**
     * 根据多个条件删除数据  谨记操作信息的写入
     * @param columnMap 表字段 map 对象
     * @return
     */
    @Override
    public boolean deleteByMap(Map<String, Object> columnMap) {
        if (CollectionUtils.isEmpty(columnMap)) {
            throw new WutosException("deleteByMap columnMap is empty.");
        }
        return retBool(baseMapper.deleteByMap(columnMap));
    }

    /**
     * 根据id列表删除数据
     * @param idList 主键ID列表
     * @return
     */
    @Override
    public boolean deleteBatchIds(List<? extends Serializable> idList) {
        for(Serializable id:idList){
            T entity = selectById(id);
            return delete(entity);
        }
        return false;
    }
    /**
     *
     * 修改数据前执行此方法
     * @param entity 实体对象
     * @return
     */
    public  void preUpdate(T entity){};
    public  void preUpdate(Map map){};
    @Override
    public boolean updateById(T entity) {
        setUserAndTimeValue("update",entity);
        preUpdate(entity);
        return retBool(baseMapper.updateById(entity));
    }

    @Override
    public boolean updateByMap(Map map) {
        preUpdate(map);
        return retBool(baseMapper.updateByMap(map));
    }

    @Override
    public T selectById(Serializable id) {
        if (StringUtils.isEmpty(id.toString())) {
            throw new RuntimeException("id is empty.");
        }
        return baseMapper.selectById(id);
    }

    /**
     * 通过主键id列表删除数据
     * @param idList 主键ID列表
     * @return
     */
    @Override
    public List<T> selectBatchIds(List<? extends Serializable> idList) {
        return baseMapper.selectBatchByIds(idList);
    }

    /**
     * 多条件查询数据
     * @param columnMap 表字段 map 对象
     * @return
     */
    @Override
    public List<T> selectByMap(Map<String, Object> columnMap) {
        return baseMapper.selectByMap(columnMap);
    }

    /**
     * 查询所有数据
     * @return
     */
    @Override
    public List<T> selectAll() {
        return baseMapper.selectAll();
    }

    /**
     * 根据租户信息查询所有数据
     * @param tenantId
     * @return
     */
    @Override
    public List<T> selectAllByTenantId(Integer tenantId) {
        return baseMapper.selectAllByTenantId(tenantId);
    }

    /**
     * 通过多条件查询数据
     * @param fields
     * @return
     */
    @Override
    public List<T> selectList(Map<String,Object> fields) {
        return baseMapper.selectList(fields);
    }

    /**
     * 分页查询
     * @param page "pageNum":第几页,
     *             "pageSize":每页多少行,
     *             "param":{
     *             "type":类型ID,
     *             "keyword":搜索关键字
     *             }
     * @return
     */
    @Override
    public PageInfo<T> pageList(RequestPage page) {
        PageHelper.startPage(page.getPageNum(),page.getPageSize(),page.getSort());
        List<T> list = selectList(page.getParam());
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}

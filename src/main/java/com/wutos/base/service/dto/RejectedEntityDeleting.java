package com.wutos.base.service.dto;

import com.wutos.base.domain.entity.Entity;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拒绝删除实体对象
 */
public class RejectedEntityDeleting extends RuntimeException {

    static ThreadLocal<RejectedEntityDeleting> instance = new ThreadLocal<>();
    /**
     * key1:deletingEntity,
     * key2:entity，
     * value:RejectedDeletingDetail
     */
    private Map<Entity, Map<Entity, List<RejectedDeletingDetail>>> details = new HashMap<>();
    /**
     * 获取当前实例
     *
     * @return 当前实例
     */
    public static RejectedEntityDeleting getCurrentInstance() {
        if (instance.get() == null) {
            instance.set(new RejectedEntityDeleting());
        }
        return instance.get();
    }

    public static boolean isValidated() {
        //当前线程没有RejectedDeletingDetail实例或者details为空说明没有拒绝删除的对象 可以正常删除
        if (instance.get() == null || CollectionUtils.isEmpty(instance.get().details)) {
            return true;
        }
        return false;
    }

    /**
     * 获取新实例
     *
     * @return 新实例
     */
    public static void ClearValue() {
        RejectedEntityDeleting.getCurrentInstance().details.clear();
    }

    public Map<Entity, Map<Entity, List<RejectedDeletingDetail>>> getDetails() {
        return details;
    }

    /**
     * @param deletingEntity
     * @param deletingClazzlabel
     * @param deletingEntityLabel
     * @param entity
     * @param clazzLabel
     * @param entityLabel
     * @param propertyLabel
     */
    public void addDetail(Entity deletingEntity, String deletingClazzlabel, String deletingEntityLabel,
                          Entity entity, String clazzLabel, String entityLabel, String propertyLabel) {
        Map<Entity, List<RejectedDeletingDetail>> detailmap = details.get(deletingEntity);
        if (detailmap == null) {
            detailmap = new HashMap<>();
            details.put(deletingEntity,detailmap);
        }
        List<RejectedDeletingDetail> detailList = detailmap.get(entity);
        if (detailList == null) {
            detailList = new ArrayList<>();
            detailmap.put(entity,detailList);
        }
        detailList.add(new RejectedDeletingDetail(deletingEntity, deletingClazzlabel, deletingEntityLabel,
                entity, clazzLabel, entityLabel, propertyLabel));
    }


    /**
     * 拒绝删除明细消息
     */
    public class RejectedDeletingDetail {
        //要刪除的实体
        public Entity deletingEntity;
        //要删除实体类clazz名称
        public String deletingClazzlabel;
        //要删除实体的描述  一般是name deletingEntity。getName();
        public String deletingEntityLabel;
        //依赖要删除实体的对象
        public Entity entity;
        //依赖类的clazz描述
        public String clazzLabel;
        //依赖实体的描述  一般为实体的name  entity.getName()
        public String entityLabel;
        //属性描述
        public String propertyLabel;

        public RejectedDeletingDetail(Entity deletingEntity, String deletingClazzlabel, String deletingEntityLabel, Entity entity, String clazzLabel, String entityLabel, String propertyLabel) {
            this.deletingEntity = deletingEntity;
            this.deletingClazzlabel = deletingClazzlabel;
            this.deletingEntityLabel = deletingEntityLabel;
            this.entity = entity;
            this.clazzLabel = clazzLabel;
            this.entityLabel = entityLabel;
            this.propertyLabel = propertyLabel;
        }
    }


}

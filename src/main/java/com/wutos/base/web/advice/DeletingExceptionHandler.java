package com.wutos.base.web.advice;

import com.alibaba.fastjson.JSONArray;
import com.wutos.base.domain.entity.Entity;
import com.wutos.base.service.dto.EntityTreeNode;
import com.wutos.base.service.dto.RejectedEntityDeleting;
import com.wutos.base.common.util.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 抛出RejectedEntityDeleting自定义异常处理
 * 
 * @author myc
 * @date 2018/4/12.
 */
@ControllerAdvice
@RestController
public class DeletingExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeletingExceptionHandler.class);

    /**
     * 删除校验异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(RejectedEntityDeleting.class)
    @ResponseBody
    public BaseResponse handleException(RejectedEntityDeleting exception) {
        logger.info("删除校验异常处理-RejectedEntityDeleting");
        //exception.printStackTrace();
        return BaseResponse.getInstance(4, buildErrorStr(exception));
    }


    private String buildErrorStr(RejectedEntityDeleting exception){
        JSONArray jsonArray = new JSONArray();
        for(Map.Entry<Entity, Map<Entity, List<RejectedEntityDeleting.RejectedDeletingDetail>>> entity: exception.getDetails().entrySet()){
            //entity map类型Map<Entity, Map<Entity, List<RejectedEntityDeleting.RejectedDeletingDetail>>

            Set<Map.Entry<Entity, List<RejectedEntityDeleting.RejectedDeletingDetail>>> entitySet = entity.getValue().entrySet();
            //entitySet map类型Map<Entity, List<RejectedEntityDeleting.RejectedDeletingDetail>>

            //取出entitySet 中List里面的值
            RejectedEntityDeleting.RejectedDeletingDetail entityKey = entitySet.iterator().next().getValue().get(0);

            EntityTreeNode treeNode = new EntityTreeNode("刪除 "+entityKey.deletingClazzlabel+" ("+ entityKey.deletingEntityLabel+") 失败，有如下正在使用：");
            List<EntityTreeNode> childList = new ArrayList<>();
            treeNode.setChildPanel(childList);
            for(Map.Entry<Entity, List<RejectedEntityDeleting.RejectedDeletingDetail>> detaiEntity: entitySet){
                RejectedEntityDeleting.RejectedDeletingDetail entityKey1 = detaiEntity.getValue().get(0);
                EntityTreeNode chiledL1 = new EntityTreeNode(entityKey1.clazzLabel);
                List<EntityTreeNode> chiledL1List = new ArrayList<>();
                chiledL1.setChildPanel(chiledL1List);
                childList.add(chiledL1);

                for(RejectedEntityDeleting.RejectedDeletingDetail detail :  detaiEntity.getValue()){
                    EntityTreeNode chiledL2 = new EntityTreeNode(detail.entityLabel+" 中的属性： "+detail.propertyLabel);
                    List<EntityTreeNode> chiledL2List = new ArrayList<>();
                    chiledL2.setChildPanel(chiledL2List);
                    chiledL1List.add(chiledL2);
                }
            }
            jsonArray.add(treeNode);
        }
        return jsonArray.toJSONString();
    }

}

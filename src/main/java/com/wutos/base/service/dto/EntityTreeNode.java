package com.wutos.base.service.dto;

import java.util.List;

public class EntityTreeNode {

        private String name;
        private List<EntityTreeNode> childPanel;

    public EntityTreeNode(String name) {
        this.name = name;
    }

    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<EntityTreeNode> getChildPanel() {
            return childPanel;
        }

        public void setChildPanel(List<EntityTreeNode> childPanel) {
            this.childPanel = childPanel;
        }
}

package org.commons.util;

public enum ActorOperation {

    CREATE_NODE("createNode"),
    UPDATE_NODE("updateNode"),
    DELETE_NODE("deleteNode"),

    CREATE_RELATION("createRelation"),
    UPDATE_RELATION("updateRelation"),
    DELETE_RELATION("deleteRelation");


    private String value;

    private ActorOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

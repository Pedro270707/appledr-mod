package net.pedroricardo.util;

import carpet.helpers.EntityPlayerActionPack;

public enum AIActionType {
    USE(EntityPlayerActionPack.ActionType.USE),
    ATTACK(EntityPlayerActionPack.ActionType.ATTACK),
    JUMP(EntityPlayerActionPack.ActionType.JUMP),
    DROP_ITEM(EntityPlayerActionPack.ActionType.DROP_ITEM),
    DROP_STACK(EntityPlayerActionPack.ActionType.DROP_STACK),
    SWAP_HANDS(EntityPlayerActionPack.ActionType.SWAP_HANDS);

    private EntityPlayerActionPack.ActionType actionType;

    AIActionType(EntityPlayerActionPack.ActionType actionType) {
        this.actionType = actionType;
    }

    public EntityPlayerActionPack.ActionType getActionType() {
        return this.actionType;
    }
}

package com.example.web.enums;

import java.util.HashMap;


/**
 * 预约状态枚举
 */
public enum ComplaintStatusEnum {
    /**
     * 待使用
     */
    待处理(1),

    /**
     * 使用中
     */
    处理完成(2);



    private final int index;

    ComplaintStatusEnum(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    private static final HashMap<Integer, ComplaintStatusEnum> MY_MAP = new HashMap<Integer, ComplaintStatusEnum>();

    static {
        for (ComplaintStatusEnum myEnum : values()) {
            MY_MAP.put(myEnum.index(), myEnum);
        }
    }

    public static ComplaintStatusEnum GetEnum(Integer v) {
        if (v == null) {
            return MY_MAP.values().stream().findFirst().get();
        }
        return MY_MAP.get(v);
    }

}


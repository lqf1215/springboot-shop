package cn.lili.modules.user.entity.enums;

/**
 * 发票类型
 *
 * @author Chopper
 * @since 2021-03-29 14:10:16
 */
public enum UserReceiptEnum {

    /**
     * 发票类型
     */
    ELECTRONIC_INVOICE("电子发票"),
    ORDINARY_INVOICE("普通发票");

    private String description;

    UserReceiptEnum(String str) {
        this.description = str;

    }

    public String description() {
        return description;
    }

}

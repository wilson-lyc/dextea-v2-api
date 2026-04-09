package cn.dextea.common.validation.group;

/**
 * 验证组定义
 */
public class ValidationGroups {

    private ValidationGroups() {}

    /**
     * 创建校验组
     */
    public interface Create {}

    /**
     * 更新校验组
     */
    public interface Update {}

    /**
     * 分页查询校验组
     */
    public interface PageQuery {}
}

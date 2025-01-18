package cn.dextea.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class AuthController {
    @GetMapping("/auth/v1/menu")
    public JSONObject getMenu() {
        String menu = "{\n" +
                "  \"code\": 200,\n" +
                "  \"msg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"menu\": [\n" +
                "      {\n" +
                "        \"type\": \"single\",\n" +
                "        \"key\": \"/dashboard\",\n" +
                "        \"name\": \"数据看板\",\n" +
                "        \"icon\": \"dashboard\",\n" +
                "        \"children\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\": \"group\",\n" +
                "        \"key\": \"/admin/store\",\n" +
                "        \"name\": \"门店管理\",\n" +
                "        \"icon\": \"shop\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"key\":\"/admin/store/list\",\n" +
                "            \"name\":\"所有门店\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\":\"group\",\n" +
                "        \"key\":\"/admin/commodity\"\n" +
                "        \"name\":\"商品管理\",\n" +
                "        \"icon\":\"commodity\",\n" +
                "        \"children\":[\n" +
                "          {\n" +
                "            \"key\":\"/admin/commodity/category\",\n" +
                "            \"name\":\"商品品类\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\":\"/admin/commodity/list\",\n" +
                "            \"name\":\"所有商品\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\":\"single\",\n" +
                "        \"key\":\"/admin/menu/list\",\n" +
                "        \"name\":\"菜单管理\",\n" +
                "        \"icon\":\"hamburger-button\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\":\"group\",\n" +
                "        \"key\":\"/admin/staff\",\n" +
                "        \"name\":\"员工管理\",\n" +
                "        \"icon\":\"file-staff-one\",\n" +
                "        \"children\":[\n" +
                "          {\n" +
                "            \"key\":\"/admin/staff/list\",\n" +
                "            \"name\":\"所有员工\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\":\"/admin/staff/role\",\n" +
                "            \"name\":\"角色\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"key\":\"/admin/staff/authority\",\n" +
                "            \"name\":\"权限列表\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\":\"group\",\n" +
                "        \"key\":\"/admin/customer\",\n" +
                "        \"name\":\"顾客管理\",\n" +
                "        \"icon\":\"light-member\",\n" +
                "        \"children\":[\n" +
                "          {\n" +
                "            \"key\":\"/admin/customer/list\",\n" +
                "            \"name\":\"所有顾客\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\":\"group\",\n" +
                "        \"key\":\"/admin/coupon\",\n" +
                "        \"name\":\"优惠券管理\",\n" +
                "        \"icon\":\"coupon\",\n" +
                "        \"children\":[\n" +
                "          {\n" +
                "            \"key\":\"/admin/coupon/list\",\n" +
                "            \"name\":\"所有优惠券\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        return JSONObject.parseObject(menu);
    }
}

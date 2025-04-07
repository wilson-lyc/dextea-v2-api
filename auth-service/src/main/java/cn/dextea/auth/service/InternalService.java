package cn.dextea.auth.service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    List<String> getStaffRoleKeys(Long id);
    List<String> getStaffPermissionKeys(Long id);
}

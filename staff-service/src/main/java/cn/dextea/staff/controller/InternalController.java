package cn.dextea.staff.controller;

import cn.dextea.staff.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/staff/internal/isStaffIdValid")
    public boolean isStaffIdValid(@RequestParam Long id){
        return internalService.isStaffIdValid(id);
    }
}

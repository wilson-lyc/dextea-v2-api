package cn.dextea.staff.util;

import cn.dextea.common.exception.MySQLException;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@Validated
public class AccountUtil {
    private final Lock lock = new ReentrantLock();
    @Autowired
    StaffMapper staffMapper;
    /**
     * 创建账号
     * @param name 姓名
     * @return 账号
     */
    @Transactional(rollbackFor = Exception.class)
    public String create(@NotBlank(message = "name is required") String name) {
        // 姓名转拼音
        String namePinyin = PinyinUtil.getPinyin(name, "");
        QueryWrapper<Staff> wrapper = new QueryWrapper<>();
        wrapper.eq("name_pinyin", namePinyin);
        try {
            lock.lock();
            Long samePinYinCount = staffMapper.selectCount(wrapper);
            String account = samePinYinCount > 0 ? namePinyin + (samePinYinCount + 1) : namePinyin;
            Staff staff = new Staff();
            staff.setName(name);
            staff.setNamePinyin(namePinyin);
            staff.setAccount(account);
            staffMapper.insert(staff);
            return account;
        } finally {
            lock.unlock();
        }
    }
}

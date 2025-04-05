package cn.dextea.staff.util;

import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.mapper.StaffMapper;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    @Resource
    StaffMapper staffMapper;
    /**
     * 创建账号
     * @param name 姓名
     * @return 账号
     */
    @Transactional(rollbackFor = Exception.class)
    public Staff create(String name) {
        // 姓名转拼音
        String namePinyin = PinyinUtil.getPinyin(name, "");
        // 生成账号
        QueryWrapper<Staff> wrapper = new QueryWrapper<Staff>().eq("name_pinyin", namePinyin);
        try {
            lock.lock();
            Long samePinYinCount = staffMapper.selectCount(wrapper);
            String account = samePinYinCount > 0 ? namePinyin + (samePinYinCount + 1) : namePinyin;
            Staff staff =Staff.builder()
                    .name(name)
                    .namePinyin(namePinyin)
                    .account(account)
                    .build();
            staffMapper.insert(staff);
            return staff;
        } finally {
            lock.unlock();
        }
    }
}

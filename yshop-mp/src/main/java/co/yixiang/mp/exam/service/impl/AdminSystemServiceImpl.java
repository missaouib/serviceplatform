package co.yixiang.mp.exam.service.impl;

import co.yixiang.mp.exam.dao.AdminMapper;
import co.yixiang.mp.exam.domain.Admin;
import co.yixiang.mp.exam.domain.AdminExample;
import co.yixiang.mp.exam.service.AdminSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSystemServiceImpl implements AdminSystemService {

    @Autowired
    AdminMapper adminMapper;

    /*
        校验管理员登录
     */
    @Override
    public List<Admin> adminLogin(String ano, String psw) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andAnoEqualTo(ano).andAdmPswEqualTo(psw);
        List<Admin> resultList = adminMapper.selectByExample(adminExample);
        return resultList;
    }

    @Override
    public boolean updateAdmin(Admin admin) {
        int result = adminMapper.updateByPrimaryKeySelective(admin);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }
}

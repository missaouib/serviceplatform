package co.yixiang.mp.exam.service;

import co.yixiang.mp.exam.domain.Admin;

import java.util.List;

public interface AdminSystemService {
    public List<Admin> adminLogin(String ano, String psw);
    public boolean updateAdmin(Admin admin);
}

package com.longlong.longlongdisplay.dao;

import com.longlong.longlongdisplay.dao.bean.Student;
import com.longlong.longlongdisplay.utils.MysqlUtil;
import com.mysql.jdbc.PreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author longlong
 * @create 2020 07 09 19:15
 */
public class JdbcDaoImpl {

    public void areaTOP5(){

    }
    public static List<Student> test(){
        String sql = "select * from tb_contacts where telephone = ? and name = ?";
        List<Student> list = new ArrayList<Student>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = MysqlUtil.getMysqlConn();
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, "123");
            pstmt.setString(2, "zhaoliu");
            rs = (ResultSet) pstmt.executeQuery();

            while (rs.next()){

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String telephone = rs.getString("telephone");

                Student student = new Student();
                student.setId(id);
                student.setName(name);
                student.setTelephone(telephone);

                list.add(student);
            }

            conn.close();
            pstmt.close();
            rs.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        List<Student> temp = new ArrayList();
        temp = JdbcDaoImpl.test();
        for (Student s : temp) {
            System.out.println(s);
        }
    }
}

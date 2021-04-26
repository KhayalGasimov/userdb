package com.company.dao.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.company.entity.User;
import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.UserDaoInter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl extends AbstractDAO implements UserDaoInter {

    private User getUser(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String email = rs.getString("email");



        return new User(id, name, surname, email);
    }


    private User getUserSimple(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String email = rs.getString("email");



        User user= new User(id, name, surname, email);
        return user;
    }

    @Override
    public List<User> getAll(String name, String surname, String email) {
        List<User> result = new ArrayList<>();
        try (Connection c = connect()) {

            String sql = "select "
                    + "  u.*,  "
                    + "  n.nationality, "
                    + "  c.name as birthplace  "
                    + "from user u "
                    + "left join country n on u.nationality_id = n.id "
                    + "left join country c on u.birthplace_id = c.id where 1=1 ";

            if(name!=null && !name.trim().isEmpty()){
                sql += " and u.name=? ";
            }

            if(surname!=null && !surname.trim().isEmpty()){
                sql += " and u.surname=? ";
            }

            if(email!=null){
                sql += " and u.email=? ";
            }


            PreparedStatement stmt = c.prepareStatement(sql);

            int i=1;
            if(name!=null && !name.trim().isEmpty()){
                stmt.setString(i, name);
                i++;//2
            }

            if(surname!=null && !surname.trim().isEmpty()){
                stmt.setString(i, surname);
                i++;//3
            }
            stmt.execute();
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                User u = getUser(rs);

                result.add(u);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public User findByEmail(String email) {
        User result = null;
        try(Connection c = connect()) {
            PreparedStatement stmt = c.prepareStatement("select * from user where email=?");
            stmt.setString(1,email);

            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                result = getUserSimple(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean updateUser(User u) {
        try (Connection c = connect()) {
            PreparedStatement stmt = c.prepareStatement("update user set name=?,surname=?,phone=?,email=?, profile_description=?, birthdate=?, birthplace_id=?  where id=?");
            stmt.setString(1, u.getName());
            stmt.setString(2, u.getSurname());
            stmt.setString(4, u.getEmail());
            stmt.setInt(8, u.getId());
            return stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeUser(int id) {
        try (Connection c = connect()) {
            Statement stmt = c.createStatement();
            return stmt.execute("delete from user where id = " + id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public User getById(int userId) {
        User result = null;
        try (Connection c = connect()) {

            Statement stmt = c.createStatement();
            stmt.execute("select "
                    + "  u.*,  "
                    + "from user u "
                    + userId);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                result = getUser(rs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private  static BCrypt.Hasher crypt = BCrypt.withDefaults();

    @Override
    public boolean addUser(User u) {
        try (Connection c = connect()) {
            PreparedStatement stmt = c.prepareStatement("insert into user(name,surname,email,) values(?,?,?)");
            stmt.setString(1, u.getName());
            stmt.setString(2, u.getSurname());
            stmt.setString(4, u.getEmail());
            return stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
//        User u = new User(0, "test","test","test","test",null,null,null,null);
//        new UserDaoImpl().addUser(u);

        System.out.println(crypt.hashToString(4, "123456".toCharArray()));
    }

}

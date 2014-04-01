package cn.edu.zju.isst.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import cn.edu.zju.isst.entity.City;
import cn.edu.zju.isst.entity.Klass;
import cn.edu.zju.isst.entity.Major;
import cn.edu.zju.isst.entity.User;
import cn.edu.zju.isst.entity.UserSummary;

@Repository
public class UserDaoImpl extends AbstractDao<User> implements UserDao {
    @Autowired
    private CityDao cityDao;

    @Override
    public User find(String username) {
        return query("SELECT * FROM users WHERE username=?", username);
    }

    @Override
    public void updateLoginLocation(User user, double longitude, double latitude) {
        String sql = "REPLACE INTO user_locations SET user_id=?, longitude=?, latitude=?, login_time=?";
        jdbcTemplate.getJdbcOperations().update(sql, new Object[] { user.getId(), longitude, latitude, new Timestamp(System.currentTimeMillis()) });
    }

    @Override
    public void synchronizeUsers() {
        int offset = 0;
        int count =  1000;
        int total = jdbcTemplate.getJdbcOperations().queryForObject("SELECT COUNT(id) FROM students", Integer.class);
        while (offset < total) {
            String sql = new StringBuilder("SELECT ss.id, ss.username, ss.name, ss.password, ss.class_id, ss.email, ss.tel, ss.sexual, cs.major_id, cs.start_year FROM students ss LEFT JOIN classes cs ON cs.id=ss.class_id ORDER BY ss.id ASC LIMIT ")
                .append(offset).append(", ").append(count).toString();
            jdbcTemplate.query(sql, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    String s = "REPLACE INTO users SET id=?, username=?, name=?, password=?, class_id=?, major_id=?, grade=?, email=?, phone=?, gender=?, qq='', company='', position='', signature=''";
                    jdbcTemplate.getJdbcOperations().update(s, new Object[] {
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getLong("class_id"),
                            rs.getLong("major_id"),
                            rs.getInt("start_year"),
                            rs.getString("email"),
                            rs.getString("tel"),
                            rs.getString("sexual").equals("女") ? 2 : 1
                    });
                }
            });
            offset += count;
        }
    }

    private void isCityPrincipal(User user) {
        if (user.getCityId() > 0) {
            City city = cityDao.find(user.getCityId());
            if (null != city && city.getUserId() == user.getId()) {
                user.setCityPrincipal(true);
            }
        }
    }

    @Override
    protected void onFind(User user) {
        isCityPrincipal(user);
    }
    
    @Override
    public UserSummary findUserSummary(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        List<UserSummary> list = jdbcTemplate.getJdbcOperations().query(sql, new Integer[] { id }, new RowMapper<UserSummary>() {
            @Override
            public UserSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserSummary summary = new UserSummary();
                summary.setId(rs.getInt("id"));
                summary.setName(rs.getString("name"));
                summary.setEmail(rs.getBoolean("private_email") ? "" : rs.getString("email"));
                summary.setPhone(rs.getBoolean("private_phone") ? "" : rs.getString("phone"));
                summary.setQq(rs.getBoolean("private_qq") ? "" : rs.getString("qq"));
                summary.setCompany(rs.getBoolean("private_company") ? "" : rs.getString("company"));
                summary.setPosition(rs.getBoolean("private_position") ? "" : rs.getString("position"));
                return summary;
            }
        });
        
        if (list.isEmpty()) {
            return null;
        }
        
        return list.get(0);
    }

    @Override
    public List<Klass> findAllClasses() {
        return queryAll("SELECT id, name FROM classes ORDER BY name ASC", ParameterizedBeanPropertyRowMapper.newInstance(Klass.class));
    }

    @Override
    public List<Major> findAllMajors() {
        return queryAll("SELECT id, name FROM class_major_fields ORDER BY name ASC", ParameterizedBeanPropertyRowMapper.newInstance(Major.class));
    }
}
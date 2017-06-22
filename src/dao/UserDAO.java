package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import connection.ConnectionFactory;
import entities.User;

public class UserDAO {

	
	public static void createUserTable() throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "CREATE TABLE IF NOT EXISTS User("
				+ "id BIGINT NOT NULL AUTO_INCREMENT primary key,"
				+ "name VARCHAR(255) NOT NULL,"
				+ "username VARCHAR(255) NOT NULL UNIQUE,"
				+ "password VARCHAR(255) NOT NULL,"
				+ "balance float NOT NULL,"
				+ "createdAt DATETIME"
				+ ");";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
		conn.close();
	}
	
	
	public static boolean makeTransfer(User sender, User receiver, float value){
		boolean success;
		Connection conn = null;
		try{
			
			conn = ConnectionFactory.getConnection();
			conn.setAutoCommit(false);
			String sqlSender = "Update user set balance = balance - ? where id=?";
			PreparedStatement stmtSender = conn.prepareStatement(sqlSender);
			stmtSender.setFloat(1, value);
			stmtSender.setInt(2, sender.getId());
			String sqlReceiver = "Update user set balance = balance + ? where id=?";
			PreparedStatement stmtReceiver = conn.prepareStatement(sqlReceiver);
			stmtReceiver.setFloat(1, value);
			stmtReceiver.setInt(2, receiver.getId());
			stmtSender.executeUpdate();
			stmtReceiver.executeUpdate();
			conn.commit();
			stmtSender.close();
			stmtReceiver.close();
			success = true;
		}catch(SQLException ex){
			System.out.println("Exceção de SQL: " + ex.getMessage());
			if(conn != null){
				try{
					conn.rollback();
				}catch(SQLException ex2){
					System.out.println("Exceção ao realizar rollback: " + ex2.getMessage());
				}	
			}
			success = false;
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException ex3) {
					System.out.println("Exceção ao fechar a conexão: " + ex3.getMessage());
				}
			}
		}
		return success;
	}
	
	
	/*
	public static boolean makeTransfer(User sender, User receiver, float value) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sqlSender = "Update user set balance = balance - ? where id=?";
		PreparedStatement stmtSender = conn.prepareStatement(sqlSender);
		stmtSender.setFloat(1, value);
		stmtSender.setInt(2, sender.getId());
		String sqlReceiver = "Update user set balance = balance + ? where id=?";
		PreparedStatement stmtReceiver = conn.prepareStatement(sqlReceiver);
		stmtReceiver.setFloat(1, value);
		stmtReceiver.setInt(2, receiver.getId());
		stmtSender.executeUpdate();
		stmtReceiver.executeUpdate();
		stmtSender.close();
		stmtReceiver.close();
		conn.close();
		return true;
	}
	*/
	
	public static void deleteUser(User user) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "Delete from user where id=?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, user.getId());
		stmt.execute();
		stmt.close();
		conn.close();
	}
	
	
	public static List<User> getAllUsers() throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		List<User> users = new LinkedList<>();
		String sql = "Select * from user";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String username = rs.getString("Username");
			String password = rs.getString("password");
			float balance = rs.getFloat("balance");
			java.util.Date createdAt = new java.util.Date(rs.getTimestamp("createdAt").getTime());
			users.add(new User(name, username, password, balance, createdAt, id));
		}
		rs.close();
		stmt.close();
		conn.close();
		return users;
	}
	
	public static boolean checkUserExists(String username) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "Select id from user where username=? LIMIT 1";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		boolean userExists = rs.next();
		rs.close();
		stmt.close();
		conn.close();
		return userExists;
	}
	
	public static User getUserByUsername(String username) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "Select * from user where username=? LIMIT 1";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		User user = null;
		if(rs.next()){
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String password = rs.getString("password");
			float balance = rs.getFloat("balance");
			java.util.Date createdAt = new java.util.Date(rs.getTimestamp("createdAt").getTime());
			user = new User(name, username, password, balance, createdAt, id);
		}
		rs.close();
		stmt.close();
		conn.close();
		return user;
	}
	
	public static User getUserByLogin(String username, String password) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "Select * from user where username=? and password=? LIMIT 1";
		User user = null;
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float balance = rs.getFloat("balance");
				java.util.Date createdAt = new java.util.Date(rs.getTimestamp("createdAt").getTime());
				user = new User(name, username, password, balance, createdAt, id);
			}
			rs.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		conn.close();
		return user;	
	}
	
	
	public static boolean insertUser(User user) throws SQLException{
		if(!checkUserExists(user.getUsername())){
			Connection conn = ConnectionFactory.getConnection();
			String sql = "INSERT INTO user(name,username,password,balance,createdAt)"
					+ " values (?,?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getUsername());
			stmt.setString(3, user.getPassword());
			stmt.setFloat(4, user.getBalance());
			stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			stmt.execute();
			stmt.close();
			conn.close();
			return true;
		}else{
			return false;
		}
	}
	
	/*
	public static boolean insertUser(User user) throws SQLException{
		Connection conn = ConnectionFactory.getConnection();
		String sql = "INSERT INTO user(name,username,password,balance,createdAt)"
				+ " values (?,?,?,?,?)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, user.getName());
		stmt.setString(2, user.getUsername());
		stmt.setString(3, user.getPassword());
		stmt.setFloat(4, user.getBalance());
		stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
		stmt.execute();
		stmt.close();
		conn.close();
		return true;
	}
	*/
	
	/*
	public static boolean insertUser(User user) throws SQLException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = dateFormat.format(System.currentTimeMillis());
		Connection conn = ConnectionFactory.getConnection();
		String sql = "INSERT INTO user(name,username,password,balancecreatedAt)"
				+ " values ('"+user.getName()+"','"+user.getUsername()+"','"
				+ user.getPassword()+"','" + user.getBalance()+ "','"
				+ date+"')";
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
		conn.close();
		return true;
	}
	*/
}

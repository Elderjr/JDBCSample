package entities;


import java.util.Date;

public class User {

	private int id;
	private String name;
	private String username;
	private String password;
	private float balance;
	private Date createdAt;
	
	public User(String name, String username, String password, float balance){
		this.name = name;
		this.username = username;
		this.balance = balance;
		this.password = password;
	}
	
	public User(String name, String username, String password, float balance, Date createdAt, int id){
		this(name, username, password, balance);
		this.createdAt = createdAt;
		this.id = id;
	}

	public int getId(){
		return this.id;
	}
	
	public String getName() {
		return name;
	}

	public String getUsername(){
		return this.username;
	}
	
	public String getPassword() {
		return password;
	}

	public float getBalance(){
		return this.balance;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
}

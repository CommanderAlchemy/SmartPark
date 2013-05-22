package com.smartpark.trash;

import java.util.ArrayList;


public class Database {
	
	private ArrayList<User> users = new ArrayList<User>();
	
	
	public Database(){
		
	}
	
	public Database(User user){
		addUser(user);
	}
	
	public Database(ArrayList<User> user){
		addUsers(user);
	}
	
	public void addUser(User user){
		users.add(user);
	}
	
	public void addUsers(ArrayList<User> user){
		for(int i = 0; i < user.size(); i++){
			addUser(user.get(i));
		}
	}
	
	public boolean checkUsername(String username){
		for(User temp: users){
			if(temp.getUsername().equals(username)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkPassword(String password){
		for(User temp: users){
			if(temp.getPassword().equals(password)){
				return true;
			}
		}
		return false;
	}
}

package main;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import dao.UserDAO;
import entities.User;

public class Main {
	
	public static void mainMenu(Scanner scanner)  throws SQLException{
		printAllUsers();
		System.out.println("1 - Registrar, 2 - Entrar, outro sair");
		int op = scanner.nextInt();
		if(op == 1){
			register(scanner);
		}else if(op == 2){
			scanner.nextLine();
			System.out.print("Username: ");
			String username = scanner.nextLine();
			System.out.print("Senha: ");
			String password = scanner.nextLine();
			User user = UserDAO.getUserByLogin(username, password);
			if(user != null){
				loggedMenu(scanner, user);
			}else{
				System.out.println("USername ou senha incorreto(s)");
			}
		}else{
			System.out.println("Sistema fechado");
		}
	}
	
	public static void register(Scanner scanner)  throws SQLException{
		scanner.nextLine();
		System.out.print("Nome: ");
		String name = scanner.nextLine();
		System.out.print("Username: ");
		String username = scanner.nextLine();
		System.out.print("Senha: ");
		String password = scanner.nextLine();
		System.out.print("Saldo inicial: ");
		float balance = scanner.nextFloat();
		boolean success = UserDAO.insertUser(new User(name, username, password, balance));
		if(success){
			System.out.println("Seu cadastro foi realizado com sucesso!");
		}else{
			System.out.println("Nao foi possivei realizar o cadastro");
		}
	}
	
	public static void loggedMenu(Scanner scanner, User loggedUser)  throws SQLException{
		System.out.println("1 - Fazer uma Transferência; 2 - Me Deletar; outro sair");
		int op = scanner.nextInt();
		if(op == 1){
			scanner.nextLine();
			System.out.print("Username do receptor: ");
			String username = scanner.nextLine();
			User receiver = UserDAO.getUserByUsername(username);
			if(receiver != null){
				System.out.print("Valor: ");
				float value = scanner.nextFloat();
				boolean success = UserDAO.makeTransfer(loggedUser, receiver, value);
				if(success){
					System.out.println("Transferencia feita com sucesso");
				}else{
					System.out.println("Nao foi possivel realizar a transferencia");
				}
			}else{
				System.out.println("Usuario nao existe!");
			}
			
		}else if(op == 2){
			UserDAO.deleteUser(loggedUser);
			System.out.println("Adeus");
		}else{
			System.out.println("Sistema fechado");
		}
	}
	
	public static void printAllUsers() throws SQLException{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println("Usuarios registrados no sistema: ");
		for(User user : UserDAO.getAllUsers()){
			System.out.println(user.getName() + "(" + user.getUsername() + ") - Registrado em: "
					+ dateFormat.format(user.getCreatedAt()));
		}
	}
	
	public static void main(String[] args) throws SQLException{
		UserDAO.createUserTable();
		Scanner scanner = new Scanner(System.in);
		mainMenu(scanner);
	}
}

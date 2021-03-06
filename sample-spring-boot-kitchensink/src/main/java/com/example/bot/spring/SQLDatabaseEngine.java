package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {

	int searchNumber() throws Exception {

		int result = 0;

		try {
				Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement("SELECT numberpress FROM number");
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					result = rs.getInt(1);
				} 
				rs.close();
				stmt.close();
				connection.close();
			} catch(Exception e) {
				System.out.println(e);
			}	

			return result;

	}

	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		String temp;
		String keywordAppend = "The number of keyword press is ";

		try {
			Connection connection = getConnection();
			//PreparedStatement stmt = connection.prepareStatement("SELECT keyword, response FROM table1 where keyword like concat('%', ?, '%')");
			//stmt.setString(1, text);
			PreparedStatement stmt = connection.prepareStatement("SELECT keyword, response FROM table1");
			PreparedStatement stmt2 = connection.prepareStatement("UPDATE number SET numberpress = numberpress + 1");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				temp = rs.getString(1);
				if(text.toLowerCase().contains(temp.toLowerCase())) {
					stmt2.executeUpdate();
					result = rs.getString(2);
				}
			}
			rs.close();
			stmt.close();
			stmt2.close();
			connection.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		if(result != null){
			return result;
		}

		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}

package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Main extends Application {
	
	Stage window;
	Scene scene1;
	Scene scene2;
	static ResultSet rs = null;
	static ArrayList<String> finalArray = new ArrayList<String>();
	
	
	public static ArrayList<String> get() throws Exception{
		try {
		Connection con = getConnection();
		PreparedStatement statement = con.prepareStatement("SELECT word , COUNT(*) FROM `wordoccurrences`.`word` GROUP BY word ORDER BY COUNT(*) DESC LIMIT 20; ");
		
		ResultSet result = statement.executeQuery();
		
		ArrayList<String> array = new ArrayList<String>();
		while(result.next()) {
			System.out.print(result.getString("word"));
			System.out.print(": ");
			System.out.println(result.getString("COUNT(*)"));
			
			array.add(result.getString("word") + " :" + result.getString("COUNT(*)"));
		}
		System.out.println("Top 20 records Selected!");
		finalArray = array;
		
		return array;
		
		}catch (Exception e) {System.out.println(e);}
		return null;
		}
		
	
	
	public static void main(String[] args) throws Exception{
		
		
		
		createTable();
		
		
		//This is the actual code that counts the words
		
		/*First I download the text from the webpage and turn it into a long string
		 */
		
		String raven ="";
		
		try {
			String webPage = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";
			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			raven=result;
			
		} catch (MalformedURLException q) {
			q.printStackTrace();
		} catch (IOException q) {
			q.printStackTrace();
		}
		
		/*This next part removes all unnecessary pieces and symbols from the text*/
		
		String ravena = raven.substring(3167,11430);
		

		
		ravena = ravena.replaceAll("<[^>]*>", "");
		ravena = ravena.replaceAll("â€™", "");
		ravena = ravena.replaceAll("&mdash;", " ");
		ravena = ravena.replaceAll(";", " ");
		ravena = ravena.replaceAll("â€œ", "");
		ravena = ravena.replaceAll("â€", "");

		ravena = ravena.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");

		
		String[] ravray = ravena.split(" ");
		
		System.out.println("I will wait to post");
		Thread.sleep(5000);
		
		post("Testing 1 post");
		
		for (int i=0; i<=ravray.length-1; i++) {
			post(ravray[i]);
			System.out.println(i +" Posted");
//			Thread.sleep();
			
		}
		System.out.println("This works");
		
		
		returnTopTW();
		System.out.println(rs + " This should be the results");
		
		get();
		
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		window = primaryStage; 
		
		Label label1=new  Label("When you press this button, I will show you the top twenty\nwords in 'The Raven' poem!");
		Button button1 = new Button ("Let's Go!");
		button1.setOnAction(e -> {
			window.setScene(scene2);});
		
		//Layout 1
		VBox layout1 = new VBox(20);
		layout1.getChildren().addAll(label1, button1);
		scene1 = new Scene(layout1, 400, 200);
		
		
		
		
		//Layout 2
		StackPane layout2 = new StackPane();
		Label label2=new  Label(finalArray.toString());
		
		layout2.getChildren().addAll(label2);
		scene2 = new Scene(layout2, 1000, 300);
		
		window.setScene(scene1);
		window.setTitle("Final Result");
		window.show();
	}
	
	
	public static void post(String w) throws Exception{
		
		
		try {
			Connection con = getConnection();
			PreparedStatement posted = con.prepareStatement("INSERT INTO word (word) VALUES ('"+w+"')");
			posted.executeUpdate();
			posted.close();

		}catch(Exception e) {
			System.out.println(e);
		}
		
		finally { System.out.println("Insert completed");
		
		}
	}
	
	public static void returnTopTW() throws Exception{
		try {
			 Connection con = getConnection();
			 PreparedStatement create = con.prepareStatement("SELECT word , COUNT(*) FROM `wordoccurrences`.`word` GROUP BY word ORDER BY COUNT(*) DESC LIMIT 20; ");
			rs = create.executeQuery();
			create.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		finally {System.out.println("Function complete.");}
	}
	
	public static void createTable() throws Exception{
		try {
			 Connection con = getConnection();
			 PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS word(word VARCHAR(50))");
			create.executeUpdate();
			create.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		finally {System.out.println("Function complete.");}
	}
	
	public static Connection getConnection() throws Exception{
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/wordoccurrences";
			String username = "george";
			String password = "1234";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, username, password);

			
			
			System.out.println("Connected");
			return conn;
		} catch(Exception e) {
			System.out.println(e);
		}
		
		
		
		
		return null;
	}
}

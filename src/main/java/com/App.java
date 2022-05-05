package com;

import java.util.ArrayList;
import java.util.Scanner;

import org.openqa.selenium.WebDriver;

public class App {
	
	private DriverHandler driver_handler;
	private Follower twitch_follower;
	private FollowerData follower_data;
	private WebDriver driver;
	
	public static void main(String[] args) {
		App application = new App();
		application.runProgram();
		System.exit(0);
	}
	
	public App() {
		System.out.println("Welcome, this program will copy followers from one twitch account to another" + "\n \n" );
		setup();
	}
	
	private void setup() {
		System.out.println("Downloading ChromeDriver to run program... ");
		
		this.driver_handler = new ChromeDriverHandler();
		driver_handler.installLatestDriver(System.getProperty("user.dir") + "\\src\\main\\chromedriver.zip");
		try {
			this.driver = driver_handler.getDriver();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		this.twitch_follower = new TwitchFollower(driver);
		this.follower_data = new TwitchDBFollowerData(driver);
	}
	
	public void runProgram() {
		followUsers(getInputData());
		this.driver.quit();
	}
	
	private ArrayList<String> getInputData() {
		
		System.out.print("Please enter username of the user who you would like to copy follows from: ");
		Scanner input = new Scanner(System.in);
		
		while(true) {
			String username_to_copy = input.nextLine();
			username_to_copy = username_to_copy.strip();
			
			ArrayList<String> followList;
			followList = (ArrayList<String>) ((TwitchDBFollowerData)this.follower_data).getFollowList(username_to_copy);
			
			if(followList.isEmpty()) {
				System.out.println();
				System.out.print("This username either doesn't exist or doesn't follow anyone, please try again: ");
			} else {
				input.close();
				return followList;
			}
		}
	}
	
	private void followUsers(ArrayList<String> data) {
		
		System.out.println();
		System.out.println("");
		
		((TwitchFollower)this.twitch_follower).login();
		((TwitchFollower)this.twitch_follower).followAccounts(data);
	}
	
	

}



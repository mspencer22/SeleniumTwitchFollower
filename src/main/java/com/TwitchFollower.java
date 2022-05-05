package com;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TwitchFollower implements Follower {
	
	private WebDriver driver;
	
	public TwitchFollower(WebDriver driver) {
		this.driver = driver;
	}	
	
	public void login() {
		driver.get("https://www.twitch.tv/");

		//wait for login button to load
		By login_button_path = By.cssSelector("button[data-a-target='login-button']");
		new WebDriverWait(driver, Duration.ofSeconds(10))
			.until(ExpectedConditions.presenceOfElementLocated(login_button_path));
		//click login button
		WebElement login_button = driver.findElement(login_button_path);
		login_button.click();
		
		System.out.print("Please log in, you have approx. 5 minutes to do so before the program will stop");
		for(int three_sec_passed = 0; three_sec_passed < 100; three_sec_passed++) {
			
			Thread timer_thread = new Thread()  {
				@Override
				public void run(){
					try {
						Thread.sleep(2800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			};
			timer_thread.run();
			
			if(isLoggedIn()) {
				//close phone pop-up to give new account a number
				try {
					By phone_number_popup_path = By.cssSelector("button[aria-label='Close modal']");
					new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(phone_number_popup_path));
					driver.findElement(phone_number_popup_path).click();
				} catch (Exception e) {
					System.out.println("No new account phone number pop-up");
					e.printStackTrace();
				}
				System.out.println("Is logged in");
				break;
			}
		}
		
		if(isLoggedIn() == false) {
			System.out.println("You did not login within time, program shutting down.");
			System.exit(0);
		}
	}

	public void followAccounts(List<String> accounts) {
		System.out.println("Following accounts from list on twitch...");
		
		int account_count = 0;
		for(String account_name : accounts) {
			account_count += 1;
			if(account_name.contains("(banned)")) {
				System.out.println(account_count + ". " + account_name + " is banned, skipping...");
				continue;
			} else {
				System.out.println(account_count + ". " + account_name);
			}
			
			followAccount(account_name);
		}
	}
	
	public void followAccount(String account_name) {
		System.out.println("Following " + account_name + " on twitch...");
		
		driver.get("https://www.twitch.tv/" + account_name);
		boolean follow_button_exists = true;
		WebElement follow_button;
		
		//allow time for subscribe and follow button to load (they load at the same time)
		while(true) {
			By subscribe_button_path = By.cssSelector("button[data-a-target='subscribe-button']");
			try {
				new WebDriverWait(driver, Duration.ofSeconds(10))
					.until(ExpectedConditions.presenceOfElementLocated(subscribe_button_path));
				break;
			} catch (NoSuchElementException e) {
				driver.navigate().refresh();
			}
		}
		
		while(true) {
			//follow using the follow button
			try {
				follow_button = driver.findElement(By.cssSelector("button[data-a-target='follow-button']"));
				follow_button.click();
			} catch (NoSuchElementException exception) {
				follow_button_exists = false;
			}
			
			//checking after to make sure follow button is gone
			try {
				driver.findElement(By.cssSelector("button[data-a-target='follow-button']"));
			} catch (Exception e) {
				System.out.println(account_name + " followed.");
				break;			
			}
		}
		
		if(follow_button_exists == false) {
			System.out.println(account_name + " is already followed. Skipping...");
		}
	}
	
	public boolean isLoggedIn() {
		if(driver.getCurrentUrl().contains("twitch.tv")) {
		} else {
			System.out.println("Cannot check login status unless user is on twitch.tv");
			return false;
		}
		
		boolean result;
		
		try {
			driver.findElement(By.cssSelector("img[alt='User Avatar']"));
			result = true;
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
}

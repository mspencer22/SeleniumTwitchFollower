package com;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.TwitchFollower;

class TwitchFollowerTest {

	private TwitchFollower follower;
	private WebDriver driver;

	@BeforeEach
    protected void setUp() throws Exception {
        System.out.println("Setting it up!");
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\chromedriver.exe");
        driver = new ChromeDriver();
		this.follower = new TwitchFollower(driver);
    }
	
	@AfterEach
	protected void tearDown() throws Exception {
		driver.quit();
	}
	
	@Test
	void givenLogin_whenMethodCalled_thenCheckLoginStatus() {
		driver.get("https://www.twitch.tv");
		
		boolean unlogged_result = this.follower.isLoggedIn() == false;
		
		this.follower.login();
		boolean logged_in_result = this.follower.isLoggedIn();
		
		assertAll("get isLoggedIn() results", () -> assertTrue(unlogged_result), () -> assertTrue(logged_in_result));
	}
	
	@Test 
	void givenFollowerName_whenMethodCalled_thenFollow() {
		driver.get("https://www.twitch.tv");
		this.follower.login();
		this.follower.followAccount("xqcow");
		
		boolean followed;
		try {
			driver.get("https://www.twitch.tv/xqcow");
			driver.findElement(By.cssSelector("button[data-a-target='follow-button']"));
			followed = false;
		} catch (Exception e) {
			followed = true;
		}
		
		assertTrue(followed);
	}


	
}

package com;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.TwitchDBFollowerData;

@TestInstance(Lifecycle.PER_CLASS)
class TwitchDBFollowerDataTest {

	private TwitchDBFollowerData follower_data;
	private WebDriver driver;

	@BeforeAll 
    protected void setUp() throws Exception {
        System.out.println("Setting it up!");
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\chromedriver.exe");
        driver = new ChromeDriver();
		this.follower_data = new TwitchDBFollowerData(driver);
    }
	 
	@AfterAll
	protected void tearDown() {
		driver.quit();
	}
	
	@Test
	void givenUserName_whenMethodCalled_thenReturnFollowerList() {
		String real_user_name = "xqcow";
		String fake_user_name = "fff";
		
		ArrayList<String> real_user_name_followings = (ArrayList<String>) this.follower_data.getFollowList(real_user_name);
		ArrayList<String> fake_user_name_followings = (ArrayList<String>) this.follower_data.getFollowList(fake_user_name);
		
		for(String user_name : real_user_name_followings) {
			System.out.println("1 " + user_name);
		}
		
		for(String user_name : fake_user_name_followings) {
			System.out.println("2 " + user_name);
		}
		System.out.println(real_user_name_followings);
		System.out.println(fake_user_name_followings);
		
		boolean result;
		//Check that real followings equal an array list with content, and the fake has no content
		if(real_user_name_followings.isEmpty() == false && fake_user_name_followings.isEmpty()) {
			result = true;
		} else {
			result = false;
		}
		
		assertTrue(result);
	}
	
	@Test
	void givenNothing_whenMethodCalled_thenReturnExistingFollowList() {
		boolean result1 = this.follower_data.getFollowList() == null;
		
		String user_name = "xqcow";
		ArrayList<String> test_follow_list = (ArrayList<String>) this.follower_data.getFollowList(user_name);
		this.follower_data.setFollowList(test_follow_list);
		
		boolean result2 = this.follower_data.getFollowList() != null;
		
		assertAll("getFollowList() results", () -> assertTrue(result1), () -> assertTrue(result2));
	}
	
	
	
}

package com;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
                                                           
public class TwitchDBFollowerData implements FollowerData {
	
	private List<String> user_follow_list;
	private WebDriver driver;
	
	public TwitchDBFollowerData(WebDriver driver) {
		this.driver = driver;
	}

	public List<String> getFollowList(String user_name) {
		List<String> follow_list;
		
		String URL = "https://www.twitchdatabase.com/following/" + user_name;
		driver.get(URL);
		
		//Finding an text heading element that tells you how many channels the user follows, or that there's no user with that name
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.my-4")));
		String search_result = driver.findElement(By.cssSelector("h2.my-4")).getText();
		
		if(search_result.toLowerCase().contains("no user found")) {
			follow_list = new ArrayList<String>();
		} else {
			List<WebElement> name_element_list = driver.findElements(By.cssSelector(".mb-4"));
			
			follow_list = this.getNamesFromElements(name_element_list);
		}
		
		
		return follow_list;
	}
		
	private List<String> getNamesFromElements(List<WebElement> element_list) {
		List<String> username_list = new ArrayList<String>();
		
		for (WebElement element : element_list) {
			String username = "";
			
			//Gets user name
			username += element.findElement(By.cssSelector(".text-truncate")).getText();
			
			//if it's a banned streamer = add " (banned)" to end of user name
			try {
				WebElement banned_user_element = element.findElement(By.cssSelector(".border-danger"));
				if(banned_user_element != null) {
					username += " (banned)";
				}
			} catch (Exception e) {
				
			}
			
			username_list.add(username);
		}
		
		return username_list;
	}
	
	public List<String> getFollowList() {
		if(this.user_follow_list == null) {
			System.out.println("Error: No follow list has been created");	
			return null;
		} 
		return this.user_follow_list;
	}
	
	public void setFollowList(List<String> follow_list) {
		this.user_follow_list = follow_list;
	}

	
	
}

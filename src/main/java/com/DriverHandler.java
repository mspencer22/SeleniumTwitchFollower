package com;

import java.net.URL;
import org.openqa.selenium.WebDriver;

interface DriverHandler {
	
	WebDriver getDriver() throws Exception;
	void setDriver(WebDriver driver);
	String getLatestDriver();
	String getLatestDriverURL();
	void installLatestDriver(String install_path);
	void installLocalSystemDriver(String install_path);
	String getInstalledChromeVersion() throws Exception;
	String getDriverURL(String version_number);
	void deleteFile(String file_path);
	void downloadZipFile(URL download_link, String install_path);
	void unzipFile(String file_location) throws Exception;

}

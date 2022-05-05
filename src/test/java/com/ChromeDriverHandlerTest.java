package com;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;

import com.ChromeDriverHandler;


class ChromeDriverHandlerTest {
	
	private ChromeDriverHandler handler = new ChromeDriverHandler();
	
	@Test
	void givenCommand_whenGetLatestDriver_thenReturnLatestDriver() {
		String test = handler.getLatestDriver();
		
		//matches a regex expression that represents the ChromeDriver version control pattern
		assertTrue(test.matches("[[0-9]+.?]+"));
	}
	
	@Test
	void givenCommand_whenGetLatestDriverURL_thenReturnLatestDriverURL() {
		String driverURL = handler.getLatestDriverURL();
		String expectedURL = "https://chromedriver.storage.googleapis.com/" + handler.getLatestDriver() + "/chromedriver_win32.zip";
		
		assertEquals(driverURL, expectedURL);
	}
	
	@Test
	void whenMethodCalled_thenReturnInstalledDriverVersion() throws Exception {
		String current_driver_version = handler.getInstalledChromeVersion();
		
		System.out.println("Installed chrome version from getInstalledChromeVersion(): " + current_driver_version);
		assertTrue(current_driver_version.matches("[[0-9]{1,5}.?]+"));
	}
	@Test
	void givenFilePath_whenCommandToDelete_thenDelete() throws Exception {
		URL download_URL = new URL(handler.getLatestDriverURL());
		String install_path = System.getProperty("user.dir") + "\\src\\main\\chromedriver_win32.zip";
		
		handler.downloadZipFile(download_URL, install_path);
		boolean file_created = new File(install_path).exists();
		
		handler.deleteFile(install_path);
		boolean file_deleted = !(new File(install_path).exists());
		
		assertAll("", 
			() -> assertTrue(file_deleted), 
			() -> assertTrue(file_created));
	}
	
	@Test
	void givenCommand_whenDownloadZipFile_thenDownloadZipFile() throws Exception {
		URL download_URL = new URL(handler.getLatestDriverURL());
		String install_path = System.getProperty("user.dir") + "\\src\\main\\chromedriver_win32.zip";
		
		handler.downloadZipFile(download_URL, install_path);
		File zip_file = new File(install_path);
		
		boolean file_created = zip_file.exists();
		
		//clean up
		handler.deleteFile(install_path);
		
		assertTrue(file_created);
	}
	
	@Test
	void givenZipFileLocation_whenUnzipFile_thenUnzipFile() throws Exception {
		URL download_URL = new URL(handler.getLatestDriverURL());
		String install_path = System.getProperty("user.dir") + "\\src\\main\\chromedriver_win32.zip";
		
		handler.downloadZipFile(download_URL, install_path);
		handler.unzipFile(install_path);
		
		String driver_file_path = System.getProperty("user.dir") + "\\src\\main\\chromedriver.exe";;
		File driver_file = new File(driver_file_path);
		
		boolean file_created = driver_file.exists();
		
		assertTrue(file_created);
	}
	
}

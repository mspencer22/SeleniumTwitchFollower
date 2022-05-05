package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeDriverHandler implements DriverHandler {

	private WebDriver driver;
	
    public void installLatestDriver(String install_path) {
		try {
			downloadZipFile(new URL(getLatestDriverURL()), install_path);
			unzipFile(install_path);
			deleteFile(install_path);
			setDriver(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}		
    }
    
    public void installLocalSystemDriver(String install_path) {
		try {
			//downloadZipFile(new URL(getDriverURL(getInstalledChromeVersion())), install_path);
			//temporary test line:
			downloadZipFile(new URL("https://chromedriver.storage.googleapis.com/100.0.4896.20/chromedriver_win32.zip"), install_path);
			unzipFile(install_path);
			deleteFile(install_path);
			setDriver(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}		
    }
	
	public String getLatestDriver() {
		
		String driver = "";
		try {
			URL url = new URL("https://chromedriver.storage.googleapis.com/LATEST_RELEASE");
			Scanner reader = new Scanner(url.openStream());
			driver =  reader.nextLine();
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return driver;
	}
	
	public String getInstalledChromeVersion() throws Exception {
		Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("reg query " + "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon " +  "/v version");
        BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(proc.getInputStream()));
		
        String s = null;
        String raw_reg_query_input = "";
        while ((s = stdInput.readLine()) != null) {
            raw_reg_query_input += s;
        }
        
        //Regex for finding index that matches Chrome version naming convention; ex: 1234.3.445.3
        Matcher m = Pattern.compile("[[0-9]{1,5}.?]+").matcher(raw_reg_query_input);
        m.find();
        int version_index = m.start();
        String current_version = raw_reg_query_input.substring(version_index);

		return current_version;	
	}
	
	public String getLatestDriverURL() {
		String version_number = getLatestDriver();
		String driverURL = "https://chromedriver.storage.googleapis.com/" + version_number + "/chromedriver_win32.zip";
		
		return driverURL;
	}
	
	public String getDriverURL(String version_number) {
		String driverURL = "https://chromedriver.storage.googleapis.com/" + version_number + "/chromedriver_win32.zip";
		return driverURL;
	}
	
	public void deleteFile(String file_path) {
		File file = new File(file_path);
		
		file.delete();
		System.out.println("File deleted");
	}
	
	public void downloadZipFile(URL download_link, String install_path) {
		
		try {
			URLConnection connection = download_link.openConnection();
			
			int content_length = connection.getContentLength();
	        System.out.println("File contentLength = " + content_length + " bytes");

	        // Requesting input data from server
	        InputStream input_stream = connection.getInputStream();

	        //Removing previous existing zip file (if applicable)
	        if(new File(install_path).exists()) {
	        	System.out.println("Previous existing zip file found, deleting..");
	        	deleteFile(install_path);
	        }
	        
	        // Open local file writer
	        OutputStream output_stream = new FileOutputStream(install_path);

	        // Limiting byte written to file per loop
	        byte[] buffer = new byte[2048];

	        // Increments file size
	        int length;
	        int downloaded = 0; 
	        //for tracking while loops
	        int count = 0;
	        // Looping until server finishes
	        while ((length = input_stream.read(buffer)) != -1) 
	        {
	        	
	            // Writing data
	            output_stream.write(buffer, 0, length);
	            downloaded+=length;
	            if(count % 200 == 0 || count == 0) {
	            	System.out.println("Download Status: " + (downloaded * 100) / (content_length * 1.0) + "%");   
	            }
	            
	            count += 1;
	        }
	        
	        input_stream.close();
	        output_stream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void unzipFile(String file_location) throws Exception {
				
		ZipInputStream input_stream = new ZipInputStream(new FileInputStream(file_location));
		ZipEntry entry = input_stream.getNextEntry();
		
		String file_destination = System.getProperty("user.dir") + "\\src\\main\\chromedriver.exe";
		File new_driver_file = new File(file_destination);
		if(new_driver_file.exists()) {
			System.out.println("Previous existing driver file found, deleting..");
			deleteFile(file_destination);
		}
		
		FileOutputStream output_stream = new FileOutputStream(new_driver_file);
        int length;
        int data_processed = 0;
        int content_length = (int)entry.getSize();
        int count = 0;
        byte[] buffer = new byte[1024];
        while ((length = input_stream.read(buffer)) > 0) {
            output_stream.write(buffer, 0, length);
            data_processed+=length;
            if(count % 200 == 0 || count == 0) {
            	System.out.println("Extraction Status: " + (data_processed * 100) / (content_length * 1.0) + "%");
            }
            
            count+=1;
        }
        
        output_stream.close();
        input_stream.close();
	}
	
	public void setDriver(WebDriver driver) {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\chromedriver.exe");
		this.driver = new ChromeDriver();
		unexpectedCloseMonitor();
	}
	
	public WebDriver getDriver() throws Exception {
		if(this.driver == null) {
			throw new Exception("Driver not initialized yet");
		} else {
			return this.driver;
		}
	}
	
	private void unexpectedCloseMonitor() {
		/*Side thread that continuously checks for the browser to be closed by the user, 
		*so the driver process can be cleaned up (which will prevent program from working again)
		*/
		Thread close_monitor_thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						driver.getTitle();
					} catch (WebDriverException e) {
							System.out.println("Browser unexpectedly closed! Ending program...");
							driver.quit();
							System.exit(MAX_PRIORITY);
					}
					
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		close_monitor_thread.start();
	}
	
}

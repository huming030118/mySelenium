package com.ken.monitor.home;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;


/**
 * https://github.com/huming030118/mySelenium.git
 * @author huming1
 *
 */
public class HomeMonitor {

	private static final Logger logger = LogManager.getLogger(HomeMonitor.class);
	
	public static void main(String[] args){
		
		try{
			
			System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");

			WebDriver driver = new ChromeDriver(chromeOptions());
			
			driver.get("http://auction.jd.com/home.html");
			
			WebElement todayElement = (new WebDriverWait(driver, 60)).until(ExpectedConditions.visibilityOfElementLocated(By.id("floor2")));
			
			List<WebElement> todayList = todayElement.findElements(By.tagName("li"));
			
			List<WebElement> itemNowList = todayElement.findElements(By.cssSelector(".item.now"));
			
			List<WebElement> itemComingList = todayElement.findElements(By.cssSelector(".item.coming"));
			
			List<WebElement> itemEndList = todayElement.findElements(By.cssSelector(".item.end"));
			
			List<WebElement> itemTitList = todayElement.findElements(By.cssSelector(".item-tit"));//植入广告
			
			logger.error("今日专场	count["+todayList.size()+"]");
			logger.error("进行中专场	count["+itemComingList.size()+"]");
			logger.error("即将开始专场count["+itemNowList.size()+"]");
			logger.error("已完成专场	count["+itemEndList.size()+"]");
			logger.error("植入广告	count["+itemTitList.size()+"]");
			
			
		}catch(Exception e){
			logger.error(e);
		}
		
	}
	
	public static ChromeOptions chromeOptions() {
		ChromeOptions options = new ChromeOptions();
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.switches",
				Arrays.asList("--start-maximized"));
		options.addArguments("--test-type", "--start-maximized");
		options.addArguments("--test-type", "--ignore-certificate-errors");
		return options;
	}
}

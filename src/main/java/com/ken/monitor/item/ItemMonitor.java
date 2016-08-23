package com.ken.monitor.item;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
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

public class ItemMonitor {

	private static final Logger logger = LogManager.getLogger(ItemMonitor.class);
	
	WebDriver driver;
	
	
	public static void main(String[] args){
		ItemMonitor i = new ItemMonitor();
		i.doWork();
	}

	public void doWork() {
		initDriver();
		
		String paimaiId = "101227841";
		
		if(StringUtils.isEmpty(paimaiId)){
			logger.error("can not be get paimaiId...");
			return;
		}
		
		//打开商品画面，并点击提交保证金按钮
		if(!openItemPageAndClickEnsureButton(paimaiId)){
			logger.error("can not be openItemPageAndClickEnsureButton...");
			return ;
		}
		
		//登录
		String afterLoginPageTitle = login();
		if(afterLoginPageTitle.equals("京东支付-请选择支付方式")){
			logger.error("["+paimaiId+"] already payed ...");
			return;
		}else if(afterLoginPageTitle.equals("保证金页")){
			logger.error("turn to the ensure page....");
		}
		
		//提交保证金订单
		String afterEnsureSubitTitle = ensureSubmit(paimaiId);
		if(afterEnsureSubitTitle.contains("京东支付")){
			String a = checkByMyEnsureListPage(paimaiId);
			if(!a.contains(paimaiId)){
				logger.error("case error myMoneyPage link is "+a);
			}else{
				logger.info("check success...");
			}
		}else{
			logger.error("can not be turn to payment page,current page title is :" + afterEnsureSubitTitle);
		}
		
		driver.quit();
		driver = null;
		
	}
	
	
	public void initDriver(){
		System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.switches",Arrays.asList("--start-maximized"));
		options.addArguments("--test-type", "--start-maximized");
		options.addArguments("--test-type", "--ignore-certificate-errors");
		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
		
	}
	

	public boolean openItemPageAndClickEnsureButton(String paimaiId){
		for(int i =0 ; i <3; i++){
			try{	
				//打开单品页
				driver.get("http://paimai.jd.com/"+paimaiId);
				logger.info("open item page, paimaiId :" +paimaiId);
				Thread.sleep(3000);
				//WebElement ensureSubmit = (new WebDriverWait(driver,60)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div[2]/div[1]/div/div[2]/div[5]/div[3]/a")));
				//点击提交保证金
				WebElement ensureSubmit = (new WebDriverWait(driver,60)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"pm-operation-ensure-n\"]/div[4]/a")));
//				WebElement ensureSubmit =driver.findElement(By.xpath("//*[@id=\"pm-operation-ensure-n\"]/div[4]/a"));
//				WebElement ensureSubmit =driver.findElement(By.id("submitEnsure"));//未开始，submitEnsure 已开始
				ensureSubmit.click();
				logger.info("click submitEnsure button on item page.");
				
				//跳转登录页
				boolean l = true;
				while(l){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(driver.getWindowHandles().size()>1){
						//切换到新页签（登录页签）
						for (String winHandle : driver.getWindowHandles()) {
							driver.switchTo().window(winHandle); // switch focus of WebDriver to the next found window handle (that's your newly opened window)
							if(driver.getTitle().contains("京东-欢迎登录")){
								logger.info("switch to new window["+driver.getTitle()+"].");
								return true;
							}
						}
					}
				}
			}catch(Exception e){
				logger.error("fail to openItemPageAndClickEnsureButton, ",e);
			}
		}
		logger.error("can not be openItemPageAndClickEnsureButton");
		return false;
	}
	
	public String login(){
		
		//输入用户名密码
		(new WebDriverWait(driver,60)).until(ExpectedConditions.visibilityOfElementLocated(By.id("loginname"))).sendKeys("mingyuan030118");
		logger.info("input loginname.");
		
		(new WebDriverWait(driver,60)).until(ExpectedConditions.visibilityOfElementLocated(By.id("nloginpwd"))).sendKeys("7f8r9.abc");
		logger.info("input password.");
		
		//点击登录
		(new WebDriverWait(driver,60)).until(ExpectedConditions.visibilityOfElementLocated(By.id("loginsubmit"))).click();
		logger.info("clieck login button.");

		
		//当前商品是否已提交过保证金
		boolean l = true;
		while(l){
			try{
				Thread.sleep(3000);
			}catch(Exception e){
				logger.error("fail to waitingForOpenEnsurePage,",e);
			}
			
			if(driver.getTitle().contains("京东支付-请选择支付方式")){
				return driver.getTitle();
			}else if(driver.getTitle().contains("保证金页")){
				return driver.getTitle();
			}
		}
		
		return "";
	}
	
	public String ensureSubmit(String paimaiId){
		
		//重试3次提交保证金
		for(int i = 0;i<=3 ;i++){
			try{
				//同意协议
				driver.findElement(By.id("xieyi")).click();
				logger.info("click agreement button.");
				Thread.sleep(3000);
				
				//点击提交
				WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"container\"]/div[5]/div[1]/div[4]/button"));
//				WebElement submitButton = driver.findElement(By.id("btn-ensure-submit"));
				submitButton.click();
				
				logger.info("click submit button on ensure page...");
				Thread.sleep(5000);
				
				String expectTitle = "";
				expectTitle = driver.getTitle();
				if(expectTitle.contains("京东支付")){
					logger.info("open payment page.");
					return driver.getTitle();
				}else{
					logger.error("xieyi is "+driver.findElement(By.id("xieyi")).isSelected());
					logger.error("submit button is "+driver.findElement(By.xpath("//*[@id=\"container\"]/div[5]/div[1]/div[4]/button")));
					logger.error("driver refresh:"+"http://pm.jd.com/ensure/"+paimaiId);
					driver.get("http://pm.jd.com/ensure/"+paimaiId);
					waitingForOpenEnsurePage();
					Thread.sleep(5000);
					logger.error("driver finished refresh..");
				}
			}catch(Exception e){
				logger.error("fail to clickSubmitButton, ",e);
			}
		}
		return driver.getTitle();
	}

	
	public void waitingForOpenEnsurePage(){
		boolean l = true;
		while(l){
			try{
				Thread.sleep(3000);
			}catch(Exception e){
				logger.error("fail to waitingForOpenEnsurePage,",e);
			}
			
			if(driver.getTitle().contains("保证金页")){
				l = false;
			}
		}
//		new WebDriverWait(driver,600).until(ExpectedConditions.invisibilityOfElementWithText(By.tagName("title"),"珍品拍卖-保证金页"));
		
		logger.info("open agreement page.");
	}
	
	public String checkByMyEnsureListPage(String paimaiId){
		
		try {
			
			//打开我的保证金列表
			driver.get("http://auction.jd.com/myMoneyList.html");
			logger.info("open myMoneyList page");
			Thread.sleep(3000);
			
			//获取最后一次保证金记录
			WebElement paimaiLink =driver.findElement(By.xpath("//*[@id='container']/div[3]/table/tbody/tr[1]/td[2]"));
			logger.info(paimaiLink);
			return paimaiLink.getText();
			
		} catch (Exception e) {
			logger.error("fail to ensure submit.currentUrl : "+driver.getCurrentUrl(),e);
		}
		return null;
	}
	
//	public String getPaimaiIds(){
//		try{
//			Calendar calendar=Calendar.getInstance();
//			SimpleDateFormat matter1=new SimpleDateFormat("yyyyMMdd");
//			calendar.setTime(new Date());
//			String todayKey = paimaiIdListCache+matter1.format(calendar.getTime());
//			calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1);
//			String yesterdayKey = paimaiIdListCache+matter1.format(calendar.getTime());
//			
//			logger.info(todayKey+":"+yesterdayKey);
//			
//			//创建今天用的list
//			if(!redisUtils.exists(todayKey)){
//				List<Long> paimaiIdList = paimaiRuleDao.queryTomorrowPaimaiIdForEnsureSubmitMonitor();
//				if(CollectionUtils.isNotEmpty(paimaiIdList)){
//					logger.info("paimaiIdList.size("+paimaiIdList.size()+")");
//					for(Long paimaiId:paimaiIdList){
//						redisUtils.lpush(todayKey, String.valueOf(paimaiId));
//					}
//				}else{
//					logger.info("queryTomorrowPaimaiIdForEnsureSubmitMonitor return empty...");
//				}
//			}
//			//删除昨天list
//			redisUtils.del(yesterdayKey);
//			return redisUtils.lpop(todayKey);
//		}catch(Exception e){
//			logger.error("fail to initPaimaiIds");
//		}
//		return null;
//	}
}

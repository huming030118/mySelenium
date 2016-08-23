package com.ken.monitor.item;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

public class MyDriver extends HtmlUnitDriver {

	public MyDriver(BrowserVersion firefox24) {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	protected WebClient modifyWebClient(WebClient client) {

		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setCssEnabled(true);
//		client.getOptions().setUseInsecureSSL(true);
//		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
//		client.getCookieManager().setCookiesEnabled(true);
//		client.setAjaxController(new NicelyResynchronizingAjaxController());
//		client.getOptions().setThrowExceptionOnScriptError(false);
//		client.getCookieManager().setCookiesEnabled(true);
		client.waitForBackgroundJavaScript(50000);

		
		return client;
	}

}

package com.jt;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class TestHttpClient {
	/**
	 * 模拟发起Get请求
	 * 1.创建HttpClient对象
	 * 2.定义Http请求路径
	 * 3.定义请求方式
	 * 4.发起request请求，获取response响应
	 * 5.判断状态码是否正确
	 * /200 正常
	 * /400 请求参数异常
	 * /404 找不到请求路径
	 * /406 返回值与页面要求不匹配
	 * /500服务器异常
	 * 6.获取响应的结果
	 * @throws Exception 
	 * @throws ClientProtocolException 
	 */
	@Test
	public void get() throws ClientProtocolException, Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String url = "http://www.cctv.com/";
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = httpClient.execute(get);
		if(response.getStatusLine().getStatusCode()==200) {
			System.out.println("跨域系统访问success");
			String result = EntityUtils.toString(response.getEntity());
			System.out.println(result);
		}
	}
}

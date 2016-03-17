package com.crawlxywy.crawl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class DownLoadFile {
	/**
	 * ���� url ����ҳ����������Ҫ�������ҳ���ļ��� ȥ���� url �з��ļ����ַ�
	 */
	public static String getFileNameByUrl(String url, String contentType) {
		// remove http://
		url = url.substring(7);
		// text/html����
		if (contentType.indexOf("html") != -1) {
			url = url.replaceAll("[\\?/:*|<>\"]", "_");
			return url;
		}
		// ��application/pdf����
		else {
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "." + contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}

	// ���ڵ��ļ���������
	private static void saveToLocal(byte[] data, String filePath) {
		try {
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
			for (int i = 0; i < data.length; i++)
				out.write(data[i]);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  String downloadFile(String url) {
		String filePath = null;
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);// 5�볬ʱ����
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		
		try {//����ط�Ҫ��һ�£���Ҫ��ôд
			url = url.substring(7);
			url = url.replaceAll("[\\?/:*|<>\"]", "_");
			filePath = "temp\\" +url;// getFileNameByUrl(url, getMethod.getResponseHeader("Content-Type").getValue());
			File file = new File(filePath);
			if (!FileUtil.ifExist(file)) {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: " + getMethod.getStatusLine());
					filePath = null;
				}
				byte[] responseBody = getMethod.getResponseBody();// ��ȡΪ�ֽ�����
				saveToLocal(responseBody, filePath);
				System.out.println("���ز��ظ��ļ�:"+filePath);
			} else {
				System.out.println("�ظ����ļ�");
			}
		} catch (HttpException e) {
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return filePath;
	}

	public static void main(String[] args) {
//		System.out.println(downloadFile("http://bikeme.duapp.com"));
	}
}

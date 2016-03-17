package com.crawlxywy.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyCrawlerMultiThread {
	public static List<Thread> childThread = new ArrayList<Thread>();
	private final static int FROM = 1;
	private final static int TO = 201;

	/**
	 * ʹ�����ӳ�ʼ�� URL ����
	 * 
	 * @return
	 * @param seeds
	 *            ����URL
	 */
	private static void initCrawlerWithSeeds(String seeds) {

		LinkQueue.addUnvisitedUrl(seeds);
	}

	/**
	 * ץȡ����
	 * 
	 * @return
	 * @param seeds
	 */
	public void crawling(String seeds) { // �������������ȡ��xxx��ͷ������

		while (!LinkQueue.unVisitedUrlsEmpty()) {// �����ö��߳�
			String visitUrl;
			// ��ͷURL������

			visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();// �ܿ첻��Ӱ���

			if (visitUrl == null)
				continue;
			DownLoadFile downLoader = new DownLoadFile();
			// ������ҳ
			downLoader.downloadFile(visitUrl);
		}
	}

	private void initCrawl() {
		LinkQueue.removeAllUnvisited();
		LinkQueue.removeAllVisited();
	}

	// main �������
	public static void main(String[] args) {
		MyCrawlerMultiThread crawler = new MyCrawlerMultiThread();
		BThread bt = null;
		AThread at = null;
		for (int j = FROM; j < TO; j++) {
			crawler.initCrawl();
			LinkFilter filter = new LinkFilter() {
				public boolean accept(String url) {
					if (url.startsWith("http://club.xywy.com/static/"))
						return true;
					else
						return false;
				}
			};
			String seeds = null;//
			seeds = "http://club.xywy.com/list_284_all_" + j + ".htm";
			// ��ʼ�� URL ����
			initCrawlerWithSeeds(seeds);

			Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
			// �µ�δ���ʵ� URL ���
			for (String link : links) {
				LinkQueue.addUnvisitedUrl(link);
			} // ������
			bt = new BThread();
			at = new AThread(bt);
			try {
				bt.start();
				at.start();
				bt.join();
			} catch (Exception e) {
				System.out.println("Exception from main");
			}
		}
	}

}

class CThread extends Thread {
	private String visitUrl;

	public CThread(String url) {
		super("[CThread] Thread");
		this.visitUrl = url;
	};

	public void run() {
		String threadName = Thread.currentThread().getName();
		try {

			DownLoadFile downLoader = new DownLoadFile();
			// ������ҳ
			downLoader.downloadFile(visitUrl);

		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}

class BThread extends Thread {
	public BThread() {
		super("[BThread] Thread");
	};

	public void run() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " start.");
		try {
			while (!LinkQueue.unVisitedUrlsEmpty()) {// �����ö��߳�
				String visitUrl;
				// ��ͷURL������

				visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();// �ܿ첻��Ӱ���

				if (visitUrl == null)
					continue;
				new CThread(visitUrl).start();
			}

		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}

class AThread extends Thread {
	BThread bt;

	public AThread(BThread bt) {
		super("[AThread] Thread");
		this.bt = bt;
	}

	public void run() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " start.");
		try {
			bt.join();
			System.out.println(threadName + " end.");
		} catch (Exception e) {
			System.out.println("Exception from " + threadName + ".run");
		}
	}
}
package com.crawlxywy.crawl;

import java.util.Set;

public class MyCrawlerSingleThread {

	private static final int FROM = 1;
	private static final int TO = 100;

	/**
	 * ʹ�����ӳ�ʼ�� URL ����
	 * 
	 * @return
	 * @param seeds
	 *            ����URL
	 */
	private void initCrawlerWithSeeds(String seeds) {

		LinkQueue.addUnvisitedUrl(seeds);
	}

	/**
	 * ץȡ����
	 * 
	 * @return
	 * @param seeds
	 */
	public void crawling(String seeds) { // �������������ȡ��xxx��ͷ������
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.startsWith("http://club.xywy.com/static/"))
					return true;
				else
					return false;
			}
		};
		// ��ʼ�� URL ����
		initCrawlerWithSeeds(seeds);

		Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
		// �µ�δ���ʵ� URL ���
		for (String link : links) {
			LinkQueue.addUnvisitedUrl(link);
		}
		while (!LinkQueue.unVisitedUrlsEmpty()) {
			// ��ͷURL������
			String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
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
		MyCrawlerSingleThread crawler = new MyCrawlerSingleThread();
		for (int j = FROM; j < TO; j++) {
			crawler.initCrawl();
			crawler.crawling("http://club.xywy.com/list_284_all_" + j + ".htm");
		}
	}
}

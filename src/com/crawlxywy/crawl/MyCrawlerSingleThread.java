package com.crawlxywy.crawl;

import java.util.Set;

public class MyCrawlerSingleThread {

	private static final int FROM = 1;
	private static final int TO = 100;

	/**
	 * 使用种子初始化 URL 队列
	 * 
	 * @return
	 * @param seeds
	 *            种子URL
	 */
	private void initCrawlerWithSeeds(String seeds) {

		LinkQueue.addUnvisitedUrl(seeds);
	}

	/**
	 * 抓取过程
	 * 
	 * @return
	 * @param seeds
	 */
	public void crawling(String seeds) { // 定义过滤器，提取以xxx开头的链接
		LinkFilter filter = new LinkFilter() {
			public boolean accept(String url) {
				if (url.startsWith("http://club.xywy.com/static/"))
					return true;
				else
					return false;
			}
		};
		// 初始化 URL 队列
		initCrawlerWithSeeds(seeds);

		Set<String> links = HtmlParserTool.extracLinks(seeds, filter);
		// 新的未访问的 URL 入队
		for (String link : links) {
			LinkQueue.addUnvisitedUrl(link);
		}
		while (!LinkQueue.unVisitedUrlsEmpty()) {
			// 队头URL出队列
			String visitUrl = (String) LinkQueue.unVisitedUrlDeQueue();
			if (visitUrl == null)
				continue;
			DownLoadFile downLoader = new DownLoadFile();
			// 下载网页
			downLoader.downloadFile(visitUrl);
		}
	}

	private void initCrawl() {

		LinkQueue.removeAllUnvisited();
		LinkQueue.removeAllVisited();
	}

	// main 方法入口
	public static void main(String[] args) {
		MyCrawlerSingleThread crawler = new MyCrawlerSingleThread();
		for (int j = FROM; j < TO; j++) {
			crawler.initCrawl();
			crawler.crawling("http://club.xywy.com/list_284_all_" + j + ".htm");
		}
	}
}

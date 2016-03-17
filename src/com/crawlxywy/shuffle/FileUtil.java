package com.crawlxywy.shuffle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class FileUtil {
	private static String filePath = "temp";//html�ļ�·��

	public static File[] getAllFiles(String filePath) {// UTF-8
		File root = new File(filePath);
		File[] files = root.listFiles();
		return files;
	}

	public static String openFile(File fileName, String encode) {
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encode));
			String szContent = "";
			String szTemp;
			while ((szTemp = bis.readLine()) != null) {
				szContent += szTemp + "\n";
			}
			bis.close();
			return szContent;
		} catch (Exception e) {
			return "";
		}
	}


	public static String getContent(File file) throws ParserException {
		String eL1 = "[0-9]{4}-[0-9]{2}-[0-9]{2}[0-9]{2}:[0-9]{2}:[0-9]{2}";// ������ʽƥ��ʱ��
		String eL2 = "[0-9]{1,2}��";

		NodeFilter titleFilter = new HasAttributeFilter("class", "fl dib fb");
		NodeFilter infoFilter = new HasAttributeFilter("class", "f12 graydeep Userinfo clearfix pl29");
		NodeFilter describeFilter = new HasAttributeFilter("class", "graydeep User_quecol pt10 mt10");// �����Լ������������õİ���
		NodeFilter answerFilter = new HasAttributeFilter("class", "Doc_dochf mb15 bc");// ��ͨ�ظ�
		NodeFilter adoptFilter = new HasAttributeFilter("class", "Doc_dochf Best_dochf bc");// �����߲��ɵĻظ�

		Parser parser1 = new Parser();
		Parser parser2 = new Parser();
		Parser parser3 = new Parser();
		Parser parser4 = new Parser();
		Parser parser5 = new Parser();
		Parser parser6 = new Parser();
		Pattern p1 = Pattern.compile(eL1);
		Pattern p2 = Pattern.compile(eL2);

		String fileContent = FileUtil.openFile(file, "GBK");
		parser1.setInputHTML(fileContent);
		parser2.setInputHTML(fileContent);
		parser3.setInputHTML(fileContent);
		parser4.setInputHTML(fileContent);
		parser5.setInputHTML(fileContent);
		parser6.setInputHTML(fileContent);
		NodeList nodes = new NodeList();
		nodes.add(parser1.extractAllNodesThatMatch(titleFilter));
		nodes.add(parser2.extractAllNodesThatMatch(infoFilter));
		nodes.add(parser3.extractAllNodesThatMatch(describeFilter));
		nodes.add(parser5.extractAllNodesThatMatch(answerFilter));
		nodes.add(parser6.extractAllNodesThatMatch(adoptFilter));

		StringBuffer textLine = new StringBuffer();
		StringBuffer splitLine = new StringBuffer();
		String date = "";
		HtmlParser.totalFileNum++;
		for (int j = 0; j < nodes.size(); j++) {
			Node textNode = (Node) nodes.elementAt(j);

			if (j == 0) {
				textLine.append(HtmlParser.totalFileNum + "|" + textNode.toPlainTextString() + "|");
			} else if (j == 1) {// ��ȡһ���֣�������Ϣ
				NodeList infoList = new NodeList();
				infoList = textNode.getChildren();
				int nodeNeed = 0;

				for (int m = 0; m < infoList.size(); m++) {// listnode�ܶ�ո�
					Node tmp = (Node) infoList.elementAt(m);
					String textTmp = tmp.toPlainTextString();
					if (nodeNeed == 4)
						break;
					String trimTextTmp = textTmp.replace("\n", "").replaceAll("\r", "").replaceAll(" ", "");
					if (trimTextTmp.length() != 0) {
						Matcher matcher = p1.matcher(trimTextTmp);
						Matcher matcher2 = p2.matcher(trimTextTmp);
						if (matcher2.matches()) {// ����淶
							trimTextTmp = trimTextTmp.replaceFirst("��", "");
						}
						if (matcher.matches()) {// ֻƥ������
							date = textTmp.replace("\n", "").replaceAll("\r", "");
						} else {
							textLine.append(trimTextTmp + "|");
						}
						nodeNeed++;
					}
				}
			} else if (j == 2) {// ���������������õİ���
				textLine.append("������ѯ������" + textNode.toPlainTextString().replaceAll("\n", "") + "|null|" + date + "|");
			} else if (j >= 3) {// ҽ����ϣ������кü���

				NodeList docAns = new NodeList();
				docAns = textNode.getChildren();
				splitLine.append(textLine.toString() + "ҽ��" + j + "|null|"
						+ docAns.elementAt(1).toPlainTextString().trim().replaceAll("\n", "") + "|"
						+ docAns.elementAt(3).toPlainTextString().trim().replaceAll("\n", "") + "|\n");

			}
		}
		// System.out.println(textLine);
		return splitLine.toString();
	}
	public static void writeContent() throws ParserException {
		File[] files = FileUtil.getAllFiles(filePath);
		
		try {
			String path = "data\\data_xywy.txt";
			File dataFile = new File(path);
			if (!dataFile.exists())
				dataFile.createNewFile();

			FileOutputStream out = new FileOutputStream(dataFile, true); // ���׷�ӷ�ʽ��true
			for(File file:files){
				String content = FileUtil.getContent(file);
				if (content == null)
					break;
				StringBuffer sb = new StringBuffer();
				sb.append(content);
				System.out.println(HtmlParser.totalFileNum);
				out.write(sb.toString().getBytes("utf-8"));// ע����Ҫת����Ӧ���ַ���*/
			}
			
			out.close();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		} finally {

		}
	}
	

}

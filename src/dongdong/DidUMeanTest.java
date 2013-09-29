package dongdong;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.util.Streams;

import dongdong.util.CharUtil;
import dongdong.util.PinYinUtil;
import dongdong.util.TxtUtil;

public class DidUMeanTest {

	public static final int THRESHOLD = 1;
	private static CompiledSpellChecker sc;

	private static IndexReader pinyinReader;
	private static IndexSearcher pinyinSearcher;
	
	private static IndexReader archiveReader;
	private static IndexSearcher archiveSearcher;
	
	private static final String ARCHIVE_INDEX = "D:\\archive_rebuild";
	
	private static final String BOT_QUERY = "botQuery.txt"; 
	private static final String RESULT = "result";
	
	private static final float PY_SCORE = 0.9f;
	
	private static Analyzer analyzer = new IKAnalyzer(false);
	
	private static final float MIN_SCORE = -12f;
	
	/**
	 * 0：输入为全英文，通过全英文拿到结果
	 * 1: 输入为全英文，直接通过拼音找到汉字，返回汉字，得分为0.9
	 * 2： 输入为全英文，通过拼音找不到汉字，进入计算模块，再把得到结果转换成汉字返回.得分为模块得分*0.9
	 * 3：输入有汉字，把汉字转换成拼音，再把拼音转成汉字返回。得分为0.9。适用同音字输错
	 * 4.输入有汉字，转成拼音后找不到对应汉字，直接通过汉字进入计算模块。得分为模块得分
	 */
	private static int type;

	private static void initReaderSearcher() {
		if (pinyinReader == null) {
			try {
				pinyinReader = IndexReader.open(FSDirectory.open(new File(BuildIndex.INDEX_PATH)));
				pinyinSearcher = new IndexSearcher(pinyinReader);
				
				archiveReader = IndexReader.open(FSDirectory.open(new File(ARCHIVE_INDEX)));
				archiveSearcher = new IndexSearcher(archiveReader);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void init() {
		try {
			readModel(BuildIndex.MODEL_FILE);
			initReaderSearcher();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public static String didUMean(String keyword) throws IOException {
		String result = null;

		Query query = new TermQuery(new Term("keyword", keyword));

		TopDocs docs = pinyinSearcher.search(query, null, 1);
		if (docs.totalHits < THRESHOLD) {
			// System.out.println(keyword +
			// " not find in keyword, enter didUMean");
			if (CharUtil.allAscChar(keyword)) {// 不含中文
				// try to find both in keyword & pinyin
				String maybeChn = getKeywordByPinyin(keyword);
				if (maybeChn != null) {
					// System.out.println("get keyword by pinyin success");
					return maybeChn;
				}
				// start = System.currentTimeMillis();
//				String bestFit = sc.didYouMean(keyword);
				String[] results = sc.didYouMean2(keyword);
				String bestFit = results[0];
				
				// end = System.currentTimeMillis();
				// System.out.println("didYouMean cost " + (end - start) +
				// "ms");

				if ((result = getKeywordByPinyin(bestFit)) != null) {
					// System.out.println("find by didUMean & pinyin");
					return result;
				}
				return bestFit;

			} else {// has chinese chars, only find in keyword
				String pinyin = PinYinUtil.getHanyuPinyin(keyword);
				// System.out.println("chinese, pinyin is " + pinyin);
				result = getKeywordByPinyin(pinyin);
				if (result != null) {
					// System.out.println("chinese, find by pinyin, result is "
					// + result);
					return result;
				} else {
					// start = System.currentTimeMillis();
					result = sc.didYouMean(keyword);
					// end = System.currentTimeMillis();
					// System.out.println("chinese, direct didUmean, result is "
					// + result + " cost " + (end - start) + "ms");
					return result;
				}

			}
		}
		return result;
	}

	public static String[] didUMean(String keyword, boolean direct)
			throws IOException {

		String result = null;

		Query query = buildQuery(keyword);
		TopDocs docs = archiveSearcher.search(query, null, 1);
		
		if (docs.totalHits > 0) {
//			System.out.println("不用提示" + keyword);
			return null;
		}
		
		if (CharUtil.allAscChar(keyword)) {// 不含中文
			// try to find both in keyword & pinyin
			String maybeChn = getKeywordByPinyin(keyword);
			if (maybeChn != null) {
				type = 1;
				return new String[]{maybeChn, String.valueOf(PY_SCORE)};//type = 1,直接从拼音获得
			}
			String[] bestFit = sc.didYouMean2(keyword);
			if (bestFit == null) {
				return null;
			} 		

			if ((result = getKeywordByPinyin(bestFit[0])) != null) {
				double score = PY_SCORE * Double.parseDouble(bestFit[1]);
				type = 2;
				return new String[]{result, String.valueOf(score)};//type=2，纠正拼音，通过拼音拿到汉字
			}
			return bestFit;

		} else {// has chinese chars
			String pinyin = PinYinUtil.getHanyuPinyin(keyword);
			result = getKeywordByPinyin(pinyin);
			if (result != null) {
				type = 3;
				return new String[]{result, String.valueOf(PY_SCORE)};//type=3，通过汉字转拼音，再转汉字拿到
			} else {
				type = 4;
				String[] temp =  sc.didYouMean2(keyword);//tpye=4，汉字去计算拿到
				if (Float.parseFloat(temp[1]) < MIN_SCORE) {
					return null;
				}
				return temp;
			}

		}
	}

	public static String getKeywordByPinyin(String pinyin) {
		String result = null;
		try {
			Query query = new TermQuery(new Term("pinyin", pinyin));
			TopDocs docs = pinyinSearcher.search(query, 1);
			if (docs.totalHits > 0) {
				Document doc = pinyinReader.document(docs.scoreDocs[0].doc);
				result = doc.get("keyword");
				// end = System.currentTimeMillis();
//				 System.out.println("getKeywordByPinyin cost " + (end - start)
				// + "ms");
				return result;
			} else {
				return null;
			}

		} catch (IOException e) {
			System.err.println("IOException in getKeywordByPinyin");
			e.printStackTrace();
		}

		return null;
	}

	private static void readModel(File file) throws ClassNotFoundException,
			IOException {

		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream bufIn = new BufferedInputStream(fileIn);
		ObjectInputStream objIn = new ObjectInputStream(bufIn);

		sc = (CompiledSpellChecker) objIn.readObject();

		fileIn.close();
		bufIn.close();
		objIn.close();
	}

	private static void warmUp() throws IOException {
		didUMean("北京盘古投资有限公司");
		getKeywordByPinyin("gongcheng");
	}

	public static void main(String[] args) throws IOException {
		
		long start, end;
		start = System.currentTimeMillis();
		DidUMeanTest test = new DidUMeanTest();
		test.init();
		warmUp();
		end = System.currentTimeMillis();
		System.out.println("init cost " + (end - start) + "ms");
		start = System.currentTimeMillis();
		try {
			readModel(BuildIndex.MODEL_FILE);
		} catch (ClassNotFoundException e) {
			System.err.println("read model exception");
		}
		end = System.currentTimeMillis();
		System.out.println("read model cost " + (end - start) + "ms");

//		simpleTest();
		test();
	}
	
	public static void test() throws IOException {
		long start, end;
		start = System.currentTimeMillis();
		long totalCost = 0;
		int count = 0;
		StringBuilder sb = new StringBuilder();
String[] queries = TxtUtil.getFileContent(BOT_QUERY).split("\n");
		
		
		for (String t : queries) {
			count ++;
			String query = t.split(":")[0];
			start = System.currentTimeMillis();
			String[] result = didUMean(query, true);
			end = System.currentTimeMillis();

			if (result != null) {
				long cost = end - start;
				totalCost += cost;
				sb.append("cost " + cost + "ms\n");
				sb.append(type + "_" + result[1] + " didUMean " + result[0] + " instead of " + query + "\n" +
						"------------------------------\n");
			}

			
			if (count % 1000 == 0) {
				System.out.println(count + " completed");
				TxtUtil.writeToFile(RESULT, sb.toString(), true);
				sb = new StringBuilder();
			}
			
		}
		
		TxtUtil.writeToFile(RESULT, "total cost:" + totalCost + ", avg cost " + totalCost/count, true);
	}
	
	public static void simpleTest() throws IOException {
		long start, end;
		start = System.currentTimeMillis();
		
		String[] tests = {"excl", "北京盘古氏投资有限公司", "gognsi", "jaav", "工司", "gongsii", "工程司"};
		for (String t : tests) {
			String query = t.split(":")[0];
			start = System.currentTimeMillis();
			String[] result = didUMean(query, true);
			end = System.currentTimeMillis();
			long cost = end - start;
			
			if (result != null) {
				System.out.println("cost " + cost + "ms\n");
				System.out.println(type + "_" + result[1] + " did u mean " + result[0] + " instead of " + query + "\n" +
						"------------------------------\n");
			}
		}
		
	}
	
	private static String getAnalyzeResult(String keyword) {
		StringBuilder sb = new StringBuilder();
		try {
			TokenStream ts = analyzer.tokenStream(null, new StringReader(keyword));
			while (ts.incrementToken()) {
				sb.append(ts.getAttribute(CharTermAttribute.class)).append(" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private static Query buildQuery(String keyword) {
		BooleanQuery query = new BooleanQuery();
		TermQuery query1 = new TermQuery(new Term("name", keyword));
		TermQuery query2 = new TermQuery(new Term("tag_name", keyword));
		TermQuery query3 = new TermQuery(new Term("corp_name", keyword));
		query.add(query1, Occur.SHOULD);
		query.add(query2, Occur.SHOULD);
		query.add(query3, Occur.SHOULD);
		
		return query;
		
	}

}

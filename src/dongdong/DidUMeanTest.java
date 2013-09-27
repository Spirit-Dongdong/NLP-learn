package dongdong;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import com.aliasi.spell.CompiledSpellChecker;
import com.aliasi.util.Streams;

import dongdong.util.CharUtil;
import dongdong.util.PinYinUtil;
import dongdong.util.TxtUtil;

public class DidUMeanTest {

	public static final int THRESHOLD = 1;
	private static CompiledSpellChecker sc;

	private static IndexReader reader;
	private static IndexSearcher searcher;
	
	private static final String BOT_QUERY = "botQuery.txt"; 
	private static final String RESULT = "result";
	
	private static final float PY_SCORE = 0.9f;
	
	/**
	 * 1: 输入为全英文，直接通过拼音找到汉字，返回汉字，得分为0.9
	 * 2： 输入为全英文，通过拼音找不到汉字，进入计算模块，再把得到结果转换成汉字返回.得分为模块得分*0.9
	 * 3：输入有汉字，把汉字转换成拼音，再把拼音转成汉字返回。得分为0.9。适用同音字输错
	 * 4.输入有汉字，转成拼音后找不到对应汉字，直接通过汉字进入计算模块。得分为模块得分
	 * 
	 * 5.待添加。输入为全英文，通过拼音找不到汉字，直接进入计算模块返回结果。适用于objectvie c这种输入
	 */
	private static int type;

	private static void initReaderSearcher() {
		if (reader == null) {
			try {
				reader = IndexReader.open(FSDirectory.open(new File(
						BuildIndex.INDEX_PATH)));
				searcher = new IndexSearcher(reader);
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

	public static String didUMean(String keyword) throws IOException {
		String result = null;

		Query query = new TermQuery(new Term("keyword", keyword));

		TopDocs docs = searcher.search(query, null, 1);
		if (docs.totalHits < THRESHOLD) {
			// TODO: invoke "did u mean?"
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
		// long start = System.currentTimeMillis();
		// long end;

//		if (!direct) {
//			return didUMean(keyword);
//		}

		if (CharUtil.allAscChar(keyword)) {// 不含中文
			// try to find both in keyword & pinyin
			String maybeChn = getKeywordByPinyin(keyword);
			if (maybeChn != null) {
				type = 1;
				return new String[]{maybeChn, String.valueOf(PY_SCORE)};//type = 1,直接从拼音获得
			}
			// start = System.currentTimeMillis();
			String[] bestFit = sc.didYouMean2(keyword);
			// end = System.currentTimeMillis();
			// System.out.println("didYouMean cost " + (end - start) + "ms");

			if ((result = getKeywordByPinyin(bestFit[0])) != null) {
				// System.out.println("find by didUMean & pinyin");
//				return result;
				double score = PY_SCORE * Double.parseDouble(bestFit[1]);
				type = 2;
				return new String[]{result, String.valueOf(score)};//type=2，纠正拼音，通过拼音拿到汉字
			}
			return bestFit;

		} else {// has chinese chars
			String pinyin = PinYinUtil.getHanyuPinyin(keyword);
			// System.out.println("chinese, pinyin is " + pinyin);
			result = getKeywordByPinyin(pinyin);
			if (result != null) {
				// System.out.println("chinese, find by pinyin, result is " +
				// result);
				type = 3;
				return new String[]{result, String.valueOf(PY_SCORE)};//type=3，通过汉字转拼音，再转汉字拿到
			} else {
				// start = System.currentTimeMillis();
//				result = sc.didYouMean2(keyword);
				// end = System.currentTimeMillis();
				// System.out.println("chinese, direct didUmean, result is "
				// + result + " cost " + (end - start) + "ms");
				
				
//				return result;
				type = 4;
				return sc.didYouMean2(keyword);//tpye=4，汉字去计算拿到
			}

		}
	}

	public static String getKeywordByPinyin(String pinyin) {
		String result = null;
		try {
			Query query = new TermQuery(new Term("pinyin", pinyin));
			TopDocs docs = searcher.search(query, 1);
			if (docs.totalHits > 0) {
				Document doc = reader.document(docs.scoreDocs[0].doc);
				result = doc.get("keyword");
				// end = System.currentTimeMillis();
//				 System.out.println("getKeywordByPinyin cost " + (end - start)
				// + "ms");
				return result;
			} else {
				// end = System.currentTimeMillis();
				// System.out.println("not find, getKeywordByPinyin cost " +
				// (end - start) + "ms");
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
		
		long totalCost = 0;
		int count = 0;
		StringBuilder sb = new StringBuilder();
		

		start = System.currentTimeMillis();
		try {
			readModel(BuildIndex.MODEL_FILE);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("read model exception");
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("read model cost " + (end - start) + "ms");

//		String[] tests = {"北京盘古投资有限公司", "gognsi", "gongs", "工司", "gongsii", "工程司", 
//				 ""};
		
		String[] queries = TxtUtil.getFileContent(BOT_QUERY).split("\n");
		
		
//		System.out.println("-----------------------------------------");
		for (String t : queries) {
			count ++;
			String query = t.split(":")[0];
			start = System.currentTimeMillis();
			String[] result = didUMean(query, true);
			end = System.currentTimeMillis();
			long cost = end - start;
			totalCost += cost;
//			System.out.println("cost " + cost + "ms");
			sb.append("cost " + cost + "ms\n");
			if (result != null) {
//				System.out.println("did u mean " + result + " instead of " + t);
//				TxtUtil.writeToFile(RESULT, result[1] + " did u mean " + result[0] + " instead of " + query, true);
				sb.append(type + "_" + result[1] + " did u mean " + result[0] + " instead of " + query + "\n" +
						"------------------------------\n");
			}

//			System.out.println("-----------------------------------------");
//			TxtUtil.writeToFile(RESULT, "----------------------------", true);
			
			if (count % 1000 == 0) {
				System.out.println(count + " completed");
				TxtUtil.writeToFile(RESULT, sb.toString(), true);
				sb = new StringBuilder();
			}
			
		}
		
//		System.err.println("total cost:" + totalCost + ", avg cost " + totalCost/queries.length);
		TxtUtil.writeToFile(RESULT, "total cost:" + totalCost + ", avg cost " + totalCost/count, true);
	}

}

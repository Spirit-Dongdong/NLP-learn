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

public class DidUMeanTest {

	public static final int THRESHOLD = 1;
	private static CompiledSpellChecker sc;

	private static IndexReader reader;
	private static IndexSearcher searcher;

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
				String bestFit = sc.didYouMean(keyword);
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

	public static String didUMean(String keyword, boolean direct)
			throws IOException {

		String result = null;
		// long start = System.currentTimeMillis();
		// long end;

		if (!direct) {
			return didUMean(keyword);
		}

		if (CharUtil.allAscChar(keyword)) {// 不含中文
			// try to find both in keyword & pinyin
			String maybeChn = getKeywordByPinyin(keyword);
			if (maybeChn != null) {
				// System.out.println("get keyword by pinyin success");
				return maybeChn;
			}
			// start = System.currentTimeMillis();
			String bestFit = sc.didYouMean(keyword);
			// end = System.currentTimeMillis();
			// System.out.println("didYouMean cost " + (end - start) + "ms");

			if ((result = getKeywordByPinyin(bestFit)) != null) {
				// System.out.println("find by didUMean & pinyin");
				return result;
			}
			return bestFit;

		} else {// has chinese chars
			String pinyin = PinYinUtil.getHanyuPinyin(keyword);
			// System.out.println("chinese, pinyin is " + pinyin);
			result = getKeywordByPinyin(pinyin);
			if (result != null) {
				// System.out.println("chinese, find by pinyin, result is " +
				// result);
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

	public static String getKeywordByPinyin(String pinyin) {
		// long start = System.currentTimeMillis();
		// long end;
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

		// create object input stream from file
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream bufIn = new BufferedInputStream(fileIn);
		ObjectInputStream objIn = new ObjectInputStream(bufIn);

		// read the spell checker
		sc = (CompiledSpellChecker) objIn.readObject();

		fileIn.close();
		bufIn.close();
		objIn.close();
	}

	private static void warmUp() throws IOException {
		didUMean("gongis");
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
			// TODO Auto-generated catch block
			System.err.println("read model exception");
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("read model cost " + (end - start) + "ms");

		String[] tests = {"北京盘古投资有限公司", "gognsi", "gongs", "工司", "gongsii", "工程司", 
				 ""};
		System.out.println("-----------------------------------------");
		for (String t : tests) {
			start = System.currentTimeMillis();
			String result = didUMean(t, true);
			end = System.currentTimeMillis();
			System.out.println("cost " + (end - start) + "ms");
			if (result != null) {
				System.out.println("did u mean " + result + " instead of " + t);
			}

			System.out.println("-----------------------------------------");

		}
	}

}

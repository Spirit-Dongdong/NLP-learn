package dongdong;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import dongdong.util.CharUtil;
import dongdong.util.PinYinUtil;

public class DidUMeanTest {

	public static final int THRESHOLD = 5;
	
	public static String didUMean(String keyword) throws IOException {
		String result = null;
		
		IndexReader reader = IndexReader.open(FSDirectory.open(new File(BuildIndex.INDEX_PATH)));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		
		if (CharUtil.isChinese(keyword)) {
			
		}
		
		
		Query query = new TermQuery(new Term("keyword", keyword));
		
		TopDocs docs = searcher.search(query, null, 1);
		if (docs.totalHits < THRESHOLD) {
			//TODO: invoke "did u mean?"
			if (CharUtil.allAscChar(keyword)) {
				//try to find both in keyword & pinyin
				String maybeChn = PinYinUtil.getKeywordByPinyin(keyword);
				String bestFit;
			} else {//has chinese chars, only find in keyword
				String pinyin = PinYinUtil.getHanyuPinyin(keyword);
				result = PinYinUtil.getKeywordByPinyin(pinyin);
				System.out.println("chinese, find by pinyin, result is :" + result);
			}
		}
		
		
		return result;
	}
	
	public static void main(String[] args) {

	}

}

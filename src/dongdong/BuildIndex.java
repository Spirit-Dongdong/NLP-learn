package dongdong;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import dongdong.util.PinYinUtil;
import dongdong.util.TxtUtil;



public class BuildIndex {

	public static final String INDEX_PATH = "top-query";
	public static final String CORPUS = "e:\\top-query.txt";
	
	private static Analyzer analyzer = new IKAnalyzer(true);
	
	public static void buildIndex() throws IOException {
		long start = System.currentTimeMillis();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		config.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(FSDirectory.open(new File(INDEX_PATH)), config);
		
		BufferedReader br = new BufferedReader(new FileReader(new File(CORPUS)));
		String line = null;
		TokenStream ts = null;
		while ((line = br.readLine()) != null) {
			String[] pair = line.split(":");
			String content = pair[0];
			int count = Integer.parseInt(pair[1]);
			Document document = new Document();
			document.add(new IntField("count", count, Store.YES));
			
			document.add(new Field("keyword", content, Store.YES, Index.ANALYZED));
			StringBuilder pinyinSb = new StringBuilder();
			ts = analyzer.tokenStream("", new StringReader(pair[0]));
			ts.reset();
			while (ts.incrementToken()) {
				CharTermAttribute attribute = ts.getAttribute(CharTermAttribute.class);
				String term = attribute.toString();
				String pinyin = PinYinUtil.getHanyuPinyin(term);
				System.out.println(pinyin);
				pinyinSb.append(pinyin).append(" ");
			}
			document.add(new Field("pinyin", pinyinSb.toString(), Store.YES, Index.ANALYZED));
			writer.addDocument(document);
		}
		writer.commit();
		
		writer.close();
		long end = System.currentTimeMillis();
		System.out.println("use " + (end - start) + "ms");
	}
	
	
	
	public static void main(String[] args) throws IOException {
		buildIndex();
	}

}

package dongdong.didUMean;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.TrainSpellChecker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import dongdong.util.AnalyzerUtil;
import dongdong.util.PinYinUtil;



public class BuildIndex {

	public static final String INDEX_PATH = "top-query";
	public static final String CORPUS = "e:\\top-query.txt";
	public static final File MODEL_FILE = new File("resource/didUMean.model");
	
	public static final File CORP_FILE = new File("D:\\workspace\\db_related\\corpAndName.txt");
	
    static final double MATCH_WEIGHT = 3.0;
    static final double DELETE_WEIGHT = -2.0;
    static final double INSERT_WEIGHT = -1.0;
    static final double SUBSTITUTE_WEIGHT = -4.0;
    static final double TRANSPOSE_WEIGHT = -1.0;
    
    public static FixedWeightEditDistance fixedEdit;
	
	public static NGramProcessLM lm;
	public static TokenizerFactory tokenizerFactory;
	public static TrainSpellChecker tsc;
	
	public static final int MAX_NGRAM = 3;
	
	private static Analyzer analyzer;
	private static IKAnalyzer smartAnalyzer;
	
	
	public static void init() {
		analyzer = new IKAnalyzer(false);
		smartAnalyzer = new IKAnalyzer(true);
//		IKAnalyzer keyAnalyzer1 = new IKAnalyzer(useSmart);
		
		
		
		lm = new NGramProcessLM(MAX_NGRAM);
		fixedEdit = new FixedWeightEditDistance(MATCH_WEIGHT, DELETE_WEIGHT, 
				INSERT_WEIGHT, SUBSTITUTE_WEIGHT, TRANSPOSE_WEIGHT);
		tokenizerFactory = MyTokenizerFactory.INSTANCE;
		tsc = new TrainSpellChecker(lm,fixedEdit,tokenizerFactory);
	}
	
	
	
	public static void buildIndex() throws IOException {
		long start = System.currentTimeMillis();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
//		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(analyzer, fieldAnalyzers);
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
			ts = smartAnalyzer.tokenStream("", new StringReader(pair[0]));
			ts.reset();
			while (ts.incrementToken()) {
				CharTermAttribute attribute = ts.getAttribute(CharTermAttribute.class);
				String term = attribute.toString();
				String pinyin = PinYinUtil.getHanyuPinyin(term);
				pinyinSb.append(pinyin).append(" ");
			}
			if (pinyinSb.length() > 0) {
				pinyinSb.deleteCharAt(pinyinSb.length() - 1);
			}
//			System.out.println(pinyinSb);
			document.add(new Field("pinyin", pinyinSb.toString(), Store.YES, Index.NOT_ANALYZED));
			tsc.handle(content);
			tsc.handle(pinyinSb);
			
			writer.addDocument(document);
		}
		writer.commit();
		
		writer.close();
		long end = System.currentTimeMillis();
		System.out.println("index use " + (end - start) + "ms");
		
		start = System.currentTimeMillis();
		br = new BufferedReader(new FileReader(CORP_FILE));
		while ((line = br.readLine()) != null) {
//			System.out.println(line);
			String[] pair = line.split(" ");
			String corp_name = pair[0];
			
			if (pair.length > 1) {
				String name = pair[1];
				tsc.handle(name);
			}
			
			tsc.handle(corp_name);
			
		
		}
		end = System.currentTimeMillis();
		System.out.println("compile corpAndName use " + (end - start) + "ms");
	}
	
	public static void writeModel() throws IOException {
		long start = System.currentTimeMillis();
		FileOutputStream fileOut = new FileOutputStream(MODEL_FILE);
        BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
        ObjectOutputStream objOut = new ObjectOutputStream(bufOut);

        // write the spell checker to the file
        tsc.compileTo(objOut);
        
        objOut.close();
        bufOut.close();
        fileOut.close();
		long end = System.currentTimeMillis();
		System.out.println("write Model use " + (end - start) + "ms");
	}
	
	public static void main(String[] args) throws IOException {
		long start, end;
		start = System.currentTimeMillis();
		init();
		end = System.currentTimeMillis();
		System.out.println("init cost " + (end - start) + "ms");
		
		buildIndex();
		writeModel();
	}

}

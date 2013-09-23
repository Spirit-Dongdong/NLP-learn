package dongdong.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import dongdong.BuildIndex;

public class PinYinUtil {


	private static Map<String, List<String>> pinyinMap = new HashMap<String, List<String>>();

	static {
		initPinyinMap();
	}

	private static void initPinyin(String fileName) {
		System.out.println("filename:" + fileName);
		InputStream file = PinYinUtil.class.getClassLoader()
				.getResourceAsStream(fileName);
		System.out.println(file);
		// 读取多音字的全部拼音表;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {

				if (s != null) {
					String[] arr = s.split("#");
					String pinyin = arr[0];
					String chinese = arr[1];

					if (chinese != null) {
						String[] strs = chinese.split(" ");
						List<String> list = Arrays.asList(strs);
						pinyinMap.put(pinyin, list);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initPinyinMap() {
		pinyinMap.put("﻿a", getListFromString("阿 阿姨 阿富 阿门 阿拉 阿林 黑阿 麦阿密 鹿城阿岙 阿福"));
		pinyinMap.put("ao", getListFromString("拗口 违拗 凹"));
		pinyinMap.put("ai", getListFromString("艾 艾滋 艾蒿 未艾"));
		pinyinMap.put("bang", getListFromString("膀 翅膀 臂膀 重磅 磅秤 黄泥磅店 蛤蚌 蚌壳 河蚌 鹬蚌 珠蚌 蚌"));
		pinyinMap.put("ba", getListFromString("扒"));
		pinyinMap.put("bai", getListFromString("叔伯 百 百万 柏"));
		pinyinMap.put("bao", getListFromString("剥皮 薄 超薄 薄脆 薄板 薄饼 暴 暴晒 暴发 暴雨 暴力 风暴 暴露 暴风 汉堡 古堡 地堡 城堡 龍堡 卡斯堡 麻家堡 麦芬堡 汉堡 麦得堡 麦尔堡 曝光 瀑河"));
		pinyinMap.put("beng", getListFromString("蚌埠"));
		pinyinMap.put("bi", getListFromString("复辟 臂 臂章 螳臂 交臂 前臂 一臂 奋臂 膀臂 臂膀 秘鲁 泌阳"));
		pinyinMap.put("bing", getListFromString("屏弃 屏气 屏除 屏退 屏息"));
		pinyinMap.put("bian", getListFromString("扁 扁桃 便 方便 方便面 便当 便捷"));
		pinyinMap.put("bo", getListFromString("薄 薄荷 单薄 伯 伯仲 伯乐 伯劳 伯父 大伯 老伯 伯母 黄伯 伯爵 停泊 淡泊 尼泊 漂泊 波 鸿波 柏林"));
		pinyinMap.put("bu", getListFromString("大埔"));
		pinyinMap.put("can", getListFromString("参 参谋 参事 总参 参数 参议 参观 参拜 参股"));
		pinyinMap.put("cang", getListFromString("藏 埋藏 藏头 秘藏 雪藏 藏匿 收藏 馆藏 矿藏 隐藏 蕴藏 藏袍 储藏 窖藏 藏龙 藏胞 冷藏 珍藏 私藏 藏掖 藏书 藏品 伧俗 伧 龙藏寺"));
		pinyinMap.put("cen", getListFromString("参差"));
		pinyinMap.put("ceng", getListFromString("曾 不曾 似曾 几曾 何曾 曾经 曾几 未曾 噌 噌的 一声"));
		pinyinMap.put("cha", getListFromString("差 刹那 宝刹 一刹 喳喳"));
		pinyinMap.put("chai", getListFromString("公差 差役 专差 官差 听差 美差 办差 差事 差使 肥差 当差 钦差"));
		pinyinMap.put("chan", getListFromString("颤 颤悠 单于 禅 禅学 班禅 禅宗 禅堂 禅门 禅机 禅杖 禅房 禅师 坐禅 参禅 禅院"));
		pinyinMap.put("chang", getListFromString("长 周长 细长 长发 三长 长河 长袖 长衫 天长 长短 超长 长沙 长春 长安 长治 长远 长度 长江 长处 长宁 长假 长街 长征 全长 长城 波长 身长 长途 长吁 长虹 长方 厂"));
		pinyinMap.put("chao", getListFromString("朝 朝阳 朝阳区 朝鲜 朝廷 王朝 历朝 解嘲 讥嘲 自嘲 嘲笑 嘲弄 冷嘲 嘲讽 绰绰 绰起 绰家 剿袭 剿说"));
		pinyinMap.put("che", getListFromString("车 汽车 停车场 车车 黑车 车饰"));
		pinyinMap.put("chen", getListFromString("称职 匀称 称心 相称 对称"));
		pinyinMap.put("cheng", getListFromString("称 职称 简称 总称 官称 代称 称号 称谓 昵称 谦称 全称 名称 乘 噌吰 澄"));
		pinyinMap.put("chu", getListFromString("六畜 家畜 耕畜 畜生 牲畜"));
		pinyinMap.put("chui", getListFromString("椎心"));
		pinyinMap.put("chuan", getListFromString("传 文传 传媒 传销 传情 真传 祖传 传闻 传家 秘传 传单 传说"));
		pinyinMap.put("chi", getListFromString("匙子 茶匙 羹匙 汤匙 尺 尺度 英尺 咫尺 尺码 公尺 卡尺 米尺 卷尺"));
		pinyinMap.put("chong", getListFromString("重庆 重重"));
		pinyinMap.put("chou", getListFromString("臭 汗臭 臭氧 口臭 腋臭 臭虫 臭骂 臭美 酸臭 腐臭 臭气 腥臭 臭名 遗臭 恶臭 臭豆 狐臭 臭味 臭架"));
		pinyinMap.put("chuang", getListFromString("经幢"));
		pinyinMap.put("chuo", getListFromString("绰 绰约 阔绰 绰号 宽绰"));
		pinyinMap.put("ci", getListFromString("参差 伺候 龟兹"));
		pinyinMap.put("cuan", getListFromString("攒钱 攒聚 攒动"));
		pinyinMap.put("cuo", getListFromString("撮儿 撮要 撮合"));
		pinyinMap.put("da", getListFromString("大 大街 沓子 龙大 大西洋 大昌 大圣 福大 黑大 大华 大包 大厦"));
		pinyinMap.put("dao", getListFromString("叨 叨唠 絮叨 叨念 叨咕 念叨 唠叨 叨叨 磨叨"));
		pinyinMap.put("dai", getListFromString("大夫"));
		pinyinMap.put("dan", getListFromString("单 西单 东单 清单 报单 单利 名单 单姓 单亲 单线 单科 单间 单挑 单价 单词 子弹"));
		pinyinMap.put("de", getListFromString("的 似的 总的 中的 别的"));
		pinyinMap.put("deng", getListFromString("澄清"));
		pinyinMap.put("di", getListFromString("怎的 无的 有的 目的 标的 打的 的确 的当 的士 上地 大地 天地 提防 堤"));
		pinyinMap.put("diao", getListFromString("调 蓝调 蓝调吧 调调 音调 论调 格调 调令 低调 笔调 基调 强调 声调 滥调 老调 色调 单调 腔调 跑调 曲调 步调 语调 主调 情调"));
		pinyinMap.put("du", getListFromString("都 都会 国都 都城 古都 故都 大都 首都 成都 旧都 都市 龙都 鼎都 鹤都 鹏都 鸿都 麦度 度 态度 读书 法度 宽度 进度"));
		pinyinMap.put("dui", getListFromString("堆"));
		pinyinMap.put("dou", getListFromString("全都 句读"));
		pinyinMap.put("duo", getListFromString("测度 忖度 揣度 猜度 舵"));
		pinyinMap.put("dun", getListFromString("粮囤 顿"));
		pinyinMap.put("e", getListFromString("阿谀 阿胶 阿弥 恶心"));
		pinyinMap.put("er", getListFromString("儿"));
		pinyinMap.put("fan", getListFromString("番 番茄 繁"));
		pinyinMap.put("fo", getListFromString("佛 佛塔 佛徒 佛牙 佛教"));
		pinyinMap.put("fu", getListFromString("仿佛 果脯"));
		pinyinMap.put("fou", getListFromString("否 是否 与否"));
		pinyinMap.put("ga", getListFromString("咖 咖喱 伽马"));
		pinyinMap.put("gai", getListFromString("盖"));
		pinyinMap.put("gang", getListFromString("扛鼎"));
		pinyinMap.put("ge", getListFromString("革 革命 皮革 鹰革 蛤蚧 文蛤 蛤蜊 咯吱 咯噔 咯咯"));
		pinyinMap.put("gei", getListFromString("给"));
		pinyinMap.put("geng", getListFromString("脖颈"));
		pinyinMap.put("gong", getListFromString("女红"));
		pinyinMap.put("gu", getListFromString("谷 布谷 谷物 谷地 硅谷 中鹄 麦谷 麓谷 鹭谷 鼓"));
		pinyinMap.put("gui", getListFromString("龟 龟山 龟士 龟博 龟仔 鹿龟 龟汁 龟苓 龟顶"));
		pinyinMap.put("gua", getListFromString("挺括 顶呱 呱呱 呱唧 呱嗒 呱"));
		pinyinMap.put("guan", getListFromString("纶巾 东莞"));
		pinyinMap.put("guang", getListFromString("广 广州 广东 广播"));
		pinyinMap.put("ha", getListFromString("蛤蟆 癞蛤 虾蟆"));
		pinyinMap.put("hai", getListFromString("还是 还有 咳"));
		pinyinMap.put("hao", getListFromString("貉子 貉绒"));
		pinyinMap.put("hang", getListFromString("总行 分行 支行 行业 排行 行情 央行 商行 外行 银行 商行 酒行 麻行 琴行 巷道 珩"));
		pinyinMap.put("he", getListFromString("和 嘉和 和睦 亲和 龙和 之貉 威吓 恫吓 恐吓 鼎和 锦和 麒和苑 合 合资 鸿合"));
		pinyinMap.put("heng", getListFromString("道行"));
		pinyinMap.put("hu", getListFromString("鹄 鹄望 鸿鹄 鹄立"));
		pinyinMap.put("huan", getListFromString("还 鹂还"));
		pinyinMap.put("hui", getListFromString("会 会馆 会展 会所 协会 国会 会堂"));
		pinyinMap.put("hong", getListFromString("红 红装 红牌 红木 红人 虹"));
		pinyinMap.put("huo", getListFromString("软和 热和 暖和"));
		pinyinMap.put("ji", getListFromString("病革 给养 自给 给水 薪给 给予 供给 稽考 稽查 稽核 滑稽 稽留 缉获 缉查 缉私 缉捕 狼藉 奇数 亟 亟待 亟须 亟亟 亟需 诘屈 荠菜"));
		pinyinMap.put("jia", getListFromString("雪茄 伽 瑜伽 伽利略 家"));
		pinyinMap.put("jian", getListFromString("见 龙见"));
		pinyinMap.put("jiang", getListFromString("降 降温 降低 降旗 下降 倔强"));
		pinyinMap.put("jiao", getListFromString("嚼舌 嚼子 细嚼 角 平角 视角 海角 龙角 鹿角 围剿 征剿 饺 饺子 脚"));
		pinyinMap.put("jie", getListFromString("解 解放 慰藉 蕴藉 盘诘 诘难 诘问 反诘 桔"));
		pinyinMap.put("jin", getListFromString("矜 矜夸 矜持 骄矜 自矜 劲"));
		pinyinMap.put("jing", getListFromString("颈 颈项 颈椎 引颈 长颈 宫颈 瓶颈 龙颈 黑颈鹤 鹿颈 景 景色 帝景 劲松"));
		pinyinMap.put("ju", getListFromString("咀 咀嚼 居 桔汁"));
		pinyinMap.put("jun", getListFromString("均 平均 鸿均"));
		pinyinMap.put("juan", getListFromString("棚圈 圈养"));
		pinyinMap.put("jv", getListFromString("咀嚼 趑趄"));
		pinyinMap.put("jvan", getListFromString("猪圈 羊圈"));
		pinyinMap.put("jue", getListFromString("主角 角色 旦角 女角 丑角 角力 名角 配角 咀嚼 觉 直觉 感觉 错觉 触觉 幻觉 堀"));
		pinyinMap.put("jun", getListFromString("龟裂 俊"));
		pinyinMap.put("jvn", getListFromString("龟裂"));
		pinyinMap.put("ka", getListFromString("咖啡 卡 磁卡 贺卡 卡拉 胸卡 声卡 卡片 绿卡 卡通 网卡 卡口 龙卡 咯痰 咯血 喀"));
		pinyinMap.put("kang", getListFromString("扛"));
		pinyinMap.put("ke", getListFromString("咳 咳嗽 干咳 贝壳 蚌壳 外壳 蛋壳 脑壳 弹壳"));
		pinyinMap.put("keng", getListFromString("吭声 吭气 吭哧"));
		pinyinMap.put("kuai", getListFromString("会计 财会"));
		pinyinMap.put("kuo", getListFromString("括"));
		pinyinMap.put("la", getListFromString("癞痢 腊"));
		pinyinMap.put("lai", getListFromString("癞疮 癞子 癞蛤 癞皮"));
		pinyinMap.put("lao", getListFromString("积潦 络子 落枕 落价 麻粩"));
		pinyinMap.put("le", getListFromString("乐 娱乐 玩乐 乐趣 美乐 乐缘 勒 了"));
		pinyinMap.put("lei", getListFromString("勒紧"));
		pinyinMap.put("lo", getListFromString("然咯"));
		pinyinMap.put("lou", getListFromString("佝偻"));
		pinyinMap.put("long", getListFromString("里弄 弄堂 泷"));
		pinyinMap.put("li", getListFromString("礼 豊 栎"));
		pinyinMap.put("liao", getListFromString("了解 了结 明了 了得 末了 未了 了如 了如指掌 潦草 潦倒"));
		pinyinMap.put("liang", getListFromString("靓"));
		pinyinMap.put("liu", getListFromString("碌碡 碌碌 劳碌 忙碌 庸碌 六"));
		pinyinMap.put("lu", getListFromString("绿林 碌"));
		pinyinMap.put("luo", getListFromString("络 络腮 落 部落 落花 日落"));
		pinyinMap.put("lv", getListFromString("率 频率 机率 比率 效率 胜率 概率 汇率 功率 倍率 绿 绿叶 淡绿 绿色 绿豆 伛偻 绿洲"));
		pinyinMap.put("lun", getListFromString("丙纶 锦纶 经纶 涤纶"));
		pinyinMap.put("mai", getListFromString("埋"));
		pinyinMap.put("man", getListFromString("埋怨 蔓"));
		pinyinMap.put("mai", getListFromString("脉 山脉 动脉 命脉 筋脉 脉象 气脉 脉动 脉息 脉络 一脉 经脉"));
		pinyinMap.put("mang", getListFromString("氓 流氓"));
		pinyinMap.put("me", getListFromString("黛么"));
		pinyinMap.put("meng", getListFromString("群氓 盟"));
		pinyinMap.put("mei", getListFromString("没"));
		pinyinMap.put("mo", getListFromString("埋没 隐没 脉脉 模 航模 模糊 男模 楷模 规模 劳模 模型 模范 模特 名模 摩 么 麼 麽"));
		pinyinMap.put("mou", getListFromString("绸缪"));
		pinyinMap.put("mi", getListFromString("秘 秘密 秘方 奥秘 神秘 泌尿 分泌"));
		pinyinMap.put("miu", getListFromString("谬 谬论 纰缪"));
		pinyinMap.put("mu", getListFromString("人模 字模 模板 模样 模具 装模 装模做样 模子"));
		pinyinMap.put("na", getListFromString("哪 娜 安娜 娜娜 丽娜 黛尔娜 黛娜 海娜 黑娜 黄丽娜 麦香娜 优娜 麦娜 麟娜 那"));
		pinyinMap.put("nan", getListFromString("南 南方 湖南"));
		pinyinMap.put("ne", getListFromString("哪吒 呢"));
		pinyinMap.put("nong", getListFromString("弄"));
		pinyinMap.put("ni", getListFromString("毛呢 花呢 呢绒 线呢 呢料 呢子 呢喃 溺"));
		pinyinMap.put("niao", getListFromString("便溺 尿"));
		pinyinMap.put("nian", getListFromString("粘"));
		pinyinMap.put("niu", getListFromString("执拗 拗不"));
		pinyinMap.put("nue", getListFromString("疟 疟疾"));
		pinyinMap.put("nuo", getListFromString("婀娜 袅娜"));
		pinyinMap.put("nv", getListFromString("女 女人"));
		pinyinMap.put("nve", getListFromString("疟原 疟蚊"));
		pinyinMap.put("pa", getListFromString("扒"));
		pinyinMap.put("pai", getListFromString("迫击 迫击炮 派"));
		pinyinMap.put("pao", getListFromString("刨 炮"));
		pinyinMap.put("pang", getListFromString("膀胱 膀肿 磅礴"));
		pinyinMap.put("pi", getListFromString("否极 臧否 龙陂 黄陂"));
		pinyinMap.put("pian", getListFromString("扁舟 便宜"));
		pinyinMap.put("piao", getListFromString("朴姓"));
		pinyinMap.put("ping", getListFromString("屏 屏幕 荧屏 银屏"));
		pinyinMap.put("po", getListFromString("泊 迫 朴刀 坡 陂"));
		pinyinMap.put("pu", getListFromString("暴十 一曝十寒 里堡 十里堡 胸脯 肉脯 脯子 杏脯 简朴 朴质 古朴 朴厚 纯朴 朴素 诚朴 俭朴 朴实 淳朴 曝晒 瀑布 飞瀑 埔 黄埔"));
		pinyinMap.put("qiu", getListFromString("龟兹"));
		pinyinMap.put("qi", getListFromString("稽首 缉鞋 栖 奇 奇妙 传奇 亟来 荸荠 蹊跷 林栖 鹿奇 鹭奇 漆 齐 齐天大圣 齐天 其"));
		pinyinMap.put("qia", getListFromString("卡脖 卡子 关卡 卡壳 哨卡 边卡 发卡"));
		pinyinMap.put("qiao", getListFromString("雀盲 雀子 地壳 甲壳 躯壳"));
		pinyinMap.put("qian", getListFromString("纤手 拉纤 纤夫 纤绳 乾"));
		pinyinMap.put("qiang", getListFromString("强颜 强人 自强 强烈 强风 强大 黎强 麒强 鹤强 龚强"));
		pinyinMap.put("qie", getListFromString("茄子 颠茄 番茄 趔趄"));
		pinyinMap.put("qin", getListFromString("亲 亲和 亲亲 棘矜 矜锄"));
		pinyinMap.put("qing", getListFromString("干亲 亲家 黥"));
		pinyinMap.put("qu", getListFromString("区 小区"));
		pinyinMap.put("quan", getListFromString("转圈 钢圈 圆圈 罗圈 弧圈 垫圈 小圈 眼圈"));
		pinyinMap.put("que", getListFromString("雀 麻雀 鸟雀 燕雀 孔雀 云雀 雀巢、"));
		pinyinMap.put("re", getListFromString("般若"));
		pinyinMap.put("ruo", getListFromString("若"));
		pinyinMap.put("sai", getListFromString("塞 麦迪塞姆 活塞"));
		pinyinMap.put("se", getListFromString("堵塞 搪塞 茅塞 闭塞 鼻塞 梗塞 阻塞 淤塞 拥塞 哽塞 月色 彩色 特色 深色 声色 黛色 黛色 黑色瞳 色坊"));
		pinyinMap.put("sha", getListFromString("刹车 急刹 急刹车 厦 广厦 大厦 商厦 鹰大厦 莎"));
		pinyinMap.put("shai", getListFromString("色子"));
		pinyinMap.put("shan", getListFromString("姓单 单县 杉 铁杉 杉树 封禅 禅让 黒杉 栅"));
		pinyinMap.put("shang", getListFromString("裳 衣裳"));
		pinyinMap.put("she", getListFromString("拾级 折本"));
		pinyinMap.put("shen", getListFromString("沙参 野参 参王 人参 红参 丹参 山参 海参 刺参 没什 什么 为什 鹿参 身"));
		pinyinMap.put("sheng", getListFromString("野乘 千乘 史乘 省 晟 盛 盛大 鸿盛"));
		pinyinMap.put("shi", getListFromString("钥匙 拾荒 捡拾 拾物 家什 什物 什锦 麻什 麦什 喀什 牛什 识 见识 知识 似的 骨殖 食 饮食 副食 石 石业 石头 石艺 氏 姓氏 上栅 下栅"));
		pinyinMap.put("shuai", getListFromString("表率 率性 率直 率真 粗率 率领 轻率 直率 草率 大率 坦率 数字 招数 基数 数码"));
		pinyinMap.put("shuang", getListFromString("泷水"));
		pinyinMap.put("shu", getListFromString("属 金属 气数 岁数 度数 数据 级数 数控 数学 参数 次数 正数 代数 实数 系数 分数 辈数"));
		pinyinMap.put("shui", getListFromString("游说"));
		pinyinMap.put("shuo", getListFromString("数见 数见不鲜 传说 听说 妄说 实说 胡说 评说 分说 小说"));
		pinyinMap.put("si", getListFromString("窥伺 伺弄 伺机 疑似 似是 好似 似曾 形似 酷似 貌似 似懂 胜似 恰似 近似 神似 赛似 看似 活似 强似 似乎 类似 相似 思"));
		pinyinMap.put("su", getListFromString("宿主 宿命 归宿 住宿 借宿 寄宿 宿营 夜宿 露宿 投宿 宿舍 名宿 整宿 食宿"));
		pinyinMap.put("sui", getListFromString("尿泡"));
		pinyinMap.put("ta", getListFromString("拓本 拓片 碑拓 疲沓 拖沓 杂沓 沓 塔 鸿塔"));
		pinyinMap.put("tang", getListFromString("汤 鸭汤 鸡汤"));
		pinyinMap.put("tao", getListFromString("叨扰 叨光 陶 陶器"));
		pinyinMap.put("tan", getListFromString("弹性 弹力 反弹"));
		pinyinMap.put("ti", getListFromString("手提 提速 提意 提前 提早 提升 提议 提款 提婚 提包 耳提 提供 麦麦提 体"));
		pinyinMap.put("tiao", getListFromString("空调 调教 烹调 调羹 调料 调皮 调控 调节 调整 调价 谐调 协调 调色 调侃 调味 失调 调治 调频 调剂 调停 调休 调解"));
		pinyinMap.put("ting", getListFromString("町 域町 听"));
		pinyinMap.put("tong", getListFromString("垌"));
		pinyinMap.put("tui", getListFromString("褪色 褪毛"));
		pinyinMap.put("tuo", getListFromString("拓 拓宽 拓荒 开拓 落拓 拓展 拓印"));
		pinyinMap.put("tun", getListFromString("屯 囤积 囤聚"));
		pinyinMap.put("wei", getListFromString("尾 响尾 尾巴 尾灯 船尾 追尾 尾椎 月尾 燕尾 尾数 年尾 岁尾 鸢尾 凤尾 彗尾 尾翼 结尾 遗之 龙尾 齐鑫尾 麻尾 麦度 鹿尾"));
		pinyinMap.put("wu", getListFromString("可恶 交恶 好恶 厌恶 憎恶 嫌恶 痛恶 深恶"));
		pinyinMap.put("wan", getListFromString("藤蔓 枝蔓 瓜蔓 蔓儿 莞尔 万 百万 萬"));
		pinyinMap.put("xia", getListFromString("虾 虾仁 青虾 大虾 虾皮 对虾 虾子 虾酱 虾兵 虾米 龙虾 噶厦 厦门 吓唬 吓人 惊吓 天虾 龙虾 皮皮虾 麦虾"));
		pinyinMap.put("xi", getListFromString("栖栖 系 关系 星系 水系 系念 体系 联系 系列 菜系 世系 蹊 蹊径 溪 洗"));
		pinyinMap.put("xiao", getListFromString("校 学校 切削 削面 刀削 刮削"));
		pinyinMap.put("xian", getListFromString("纤细 光纤 纤巧 纤柔 纤小 纤维 纤瘦 纤纤 化纤 纤秀 棉纤 纤尘"));
		pinyinMap.put("xiang", getListFromString("巷 街巷 僻巷 巷子 龙门巷 六巷 龙湾巷 龙港巷 龙泉巷 龙巷 龙妙巷 龄巷 齐家巷 鼓楼巷 鼓巷 黎明巷 麻子巷 麻园巷 麦子巷 鹊巷"));
		pinyinMap.put("xie", getListFromString("解数 出血 采血 换血 血糊 尿血 淤血 放血 血晕 血淋 便血 吐血 咯血 叶韵 蝎 蝎子"));
		pinyinMap.put("xiu", getListFromString("铜臭 乳臭 成宿 星宿"));
		pinyinMap.put("xin", getListFromString("馨 信 鸿信"));
		pinyinMap.put("xing", getListFromString("深省 省视 内省 不省人事 省悟 省察 行 旅行 例行 行程 行乐 龙行 人行 流行 先行 行星 品行 发行 行政 风行 龙行 龍行 麟行"));
		pinyinMap.put("xu", getListFromString("牧畜 畜产 畜牧 畜养 吁 气吁 喘吁 吁吁 麦埂圩"));
		pinyinMap.put("xue", getListFromString("削 削减 削弱 削瘦 削球 削平 削价 瘦削 剥削 削职 删削 削肩 血 吸血"));
		pinyinMap.put("xun", getListFromString("荨 荨麻 荨麻疹"));
		pinyinMap.put("ya", getListFromString("芽"));
		pinyinMap.put("yao", getListFromString("发疟 疟子 约斤 称约 钥匙 金钥 耀"));
		pinyinMap.put("yan", getListFromString("吞咽 咽气 咽喉 殷红 腌 腌制 腌肉 腌菜 烟 烟草 名烟 烟酒"));
		pinyinMap.put("ye", getListFromString("抽咽 哽咽 咽炎 下咽 呜咽 幽咽 悲咽 叶 绿叶 叶子 荷叶 落叶 菜叶 红叶 树叶 枫叶 茶叶 葉 鸿葉 液"));
		pinyinMap.put("yi", getListFromString("自艾 惩艾 后尾 遗 屹"));
		pinyinMap.put("yin", getListFromString("殷 殷勤 殷墟 殷切 殷鉴"));
		pinyinMap.put("yo", getListFromString("杭育"));
		pinyinMap.put("yu", getListFromString("谷浑 呼吁 吁请 吁求 育 体育 教育 育儿 熨帖 熨烫 於"));
		pinyinMap.put("yuan", getListFromString("员"));
		pinyinMap.put("yun", getListFromString("熨 熨斗 电熨斗"));
		pinyinMap.put("yue", getListFromString("乐音 器乐 乐律 乐章 音乐 乐理 民乐 乐队 声乐 奏乐 弦乐 乐坛 管乐 配乐 乐曲 乐谱 锁钥 密钥 乐团 鼓乐社 乐器 栎阳 约 约会"));
		pinyinMap.put("zan", getListFromString("积攒"));
		pinyinMap.put("zang", getListFromString("宝藏 藏历 藏文 藏香 藏语 藏青 藏族 藏医 藏戏 藏药 藏蓝 蔵"));
		pinyinMap.put("ze", getListFromString("择 择善"));
		pinyinMap.put("zeng", getListFromString("曾孙 曾祖"));
		pinyinMap.put("za", getListFromString("绑扎 结扎 包扎 捆扎"));
		pinyinMap.put("zai", getListFromString("牛仔 龟仔 龙仔 鼻仔 羊仔 仔仔 麻仔 麵包仔 麦旺仔 鸿仔 煲仔 福仔"));
		pinyinMap.put("zha", getListFromString("扎 马扎 挣扎 扎啤 扎根 扎手 扎针 扎花 扎堆 扎营 扎实 稳扎 柞水 麻扎镇 麻扎乡 喳 栅栏"));
		pinyinMap.put("zhai", getListFromString("择菜"));
		pinyinMap.put("zhan", getListFromString("不粘 粘贴 粘连"));
		pinyinMap.put("zhao", getListFromString("朝朝 明朝 朝晖 朝夕 朝思 有朝 今朝 朝气 朝三 朝秦 朝霞 鹰爪 龙爪 魔爪 爪牙 失着 着数 龙爪槐"));
		pinyinMap.put("zhe", getListFromString("折 破折 打折 叠 曲折 折冲 存折 折合 折旧 折纸 骨折 折返 折价 折算 波折 折扇 对折 不折 折扣 七折 折中 拙著 要著 著文 新著 着 本着 对着"));
		pinyinMap.put("zhi", getListFromString("标识 嘎吱 咯吱 吱扭 吱吱 繁殖 增殖 养 生殖 殖民 枝"));
		pinyinMap.put("zhong", getListFromString("重 重量 鹏重 种"));
		pinyinMap.put("zhou", getListFromString("粥"));
		pinyinMap.put("zhu", getListFromString("属意 著录 撰著 名著 专著 著述 著作 显著 昭著 原著 著名 著书 遗著 论著 著者 编著 卓著 译著 著称"));
		pinyinMap.put("zhua", getListFromString("爪"));
		pinyinMap.put("zhui", getListFromString("椎 椎骨 尾椎 椎间 腰椎 胸椎 颈椎 脊椎"));
		pinyinMap.put("zhuo", getListFromString("执著 着装 着落 着意 着力 附着 着笔 胶着 着手 着重 穿着 衣着 执着 着眼 着墨 着实 沉着 着陆 着想 着色"));
		pinyinMap.put("zhuang", getListFromString("幢房 一幢 幢楼"));
		pinyinMap.put("zi", getListFromString("吱声 兹 来兹 今兹 仔细 仔猪"));
		pinyinMap.put("zu", getListFromString("足 沐足 足道"));
		pinyinMap.put("zuo", getListFromString("撮毛 小撮 柞绸 柞蚕 柞树 柞木"));
	}

	private static List<String> getListFromString(String string) {
		String[] elements = string.trim().split(" ");
		return Arrays.asList(elements);
	}

	public static String getHanyuPinyin(String strCN) {
		if (null == strCN) {
			return null;
		}
		StringBuffer spell = new StringBuffer();
		char[] charOfCN = strCN.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		int i = 0;
		for (; i < charOfCN.length; i++) {
			// 是否为非ASCII字符
			if (charOfCN[i] > 128) {
				String[] spellArray;
				try {
					spellArray = PinyinHelper.toHanyuPinyinStringArray(
							charOfCN[i], defaultFormat);
					if (null != spellArray) {
						if (spellArray.length == 1) {
							spell.append(spellArray[0]);
						} else if (spellArray[0].equals(spellArray[1])) {// 非多音字，有多个音
							spell.append(spellArray[0]);
						} else {// 多音字
							// spellArray = processDuoyinzi(charOfCN[i],
							// charOfCN.length);

							boolean flag = false;
							int length = charOfCN.length;
							String s = null;
							List<String> keyList = null;

							for (int x = 0; x < spellArray.length; x++) {
								String py = spellArray[x];

								keyList = pinyinMap.get(py);

								if (keyList != null) {
									if (i + 3 <= length) { // 后向匹配2个汉字 大西洋
										s = strCN.substring(i, i + 3);
										if (keyList.contains(s)) {
											spell.append(py);
											flag = true;
											break;
										}
									}
									if (i + 2 <= length) { // 后向匹配 1个汉字 大西
										s = strCN.substring(i, i + 2);
										if (keyList.contains(s)) {
											spell.append(py);
											flag = true;
											break;
										}
									}
									if ((i - 2 >= 0) && (i + 1 <= length)) { // 前向匹配2个汉字
										s = strCN.substring(i - 2, i + 1);
										if (keyList.contains(s)) {
											spell.append(py);
											flag = true;
											break;
										}
									}
									if ((i - 1 >= 0) && (i + 1 <= length)) { // 前向匹配1个汉字
										s = strCN.substring(i - 1, i + 1);
										if (keyList.contains(s)) {
											spell.append(py);
											flag = true;
											break;
										}
									}
									if ((i - 1 >= 0) && (i + 2 <= length)) { // 前向1个，后向1个
										s = strCN.substring(i - 1, i + 2);
										if (keyList.contains(s)) {
											spell.append(py);
											flag = true;
											break;
										}
									}
								}

							}// end of for
							if (!flag) { // 都没有找到，匹配默认的 读音 大
								String py = spellArray[0];
								spell.append(py);
							}
						}
					} else {// 非汉字，无法转化成拼音
						spell.append(charOfCN[i]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {

					return null;
				}

			} 
			else {//非中文字符直接append
				spell.append(charOfCN[i]);
			}
		}
		return spell.toString();
	}

	public static String[] getFirstAndHanyuPinyin(String strCN) {
		if (null == strCN) {
			return null;
		}
		StringBuffer firstSpell = new StringBuffer();
		StringBuffer spell = new StringBuffer();
		char[] charOfCN = strCN.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		int i = 0;
		for (; i < charOfCN.length; i++) {
			// 是否为非ASCII字符
			if (charOfCN[i] > 128) {
				String[] spellArray;
				try {
					spellArray = PinyinHelper.toHanyuPinyinStringArray(
							charOfCN[i], defaultFormat);
					if (null != spellArray) {
						if (spellArray.length == 1) {
							firstSpell.append(spellArray[0].charAt(0));
							spell.append(spellArray[0]);
						} else if (spellArray[0].equals(spellArray[1])) {// 非多音字，有多个音
							firstSpell.append(spellArray[0].charAt(0));
							spell.append(spellArray[0]);
						} else {// 多音字
							// spellArray = processDuoyinzi(charOfCN[i],
							// charOfCN.length);

							boolean flag = false;
							int length = charOfCN.length;
							String s = null;
							List<String> keyList = null;

							for (int x = 0; x < spellArray.length; x++) {

								String py = spellArray[x];

								keyList = pinyinMap.get(py);

								if (keyList != null) {
									if (i + 3 <= length) { // 后向匹配2个汉字 大西洋
										s = strCN.substring(i, i + 3);
										if (keyList.contains(s)) {
											spell.append(py);
											firstSpell.append(py.charAt(0));
											flag = true;
											break;
										}
									}
									if (i + 2 <= length) { // 后向匹配 1个汉字 大西
										s = strCN.substring(i, i + 2);
										if (keyList.contains(s)) {
											spell.append(py);
											firstSpell.append(py.charAt(0));
											flag = true;
											break;
										}
									}
									if ((i - 2 >= 0) && (i + 1 <= length)) { // 前向匹配2个汉字
										s = strCN.substring(i - 2, i + 1);
										if (keyList.contains(s)) {
											spell.append(py);
											firstSpell.append(py.charAt(0));
											flag = true;
											break;
										}
									}
									if ((i - 1 >= 0) && (i + 1 <= length)) { // 前向匹配1个汉字
										s = strCN.substring(i - 1, i + 1);
										if (keyList.contains(s)) {
											spell.append(py);
											firstSpell.append(py.charAt(0));
											flag = true;
											break;
										}
									}
									if ((i - 1 >= 0) && (i + 2 <= length)) { // 前向1个，后向1个
										s = strCN.substring(i - 1, i + 2);
										if (keyList.contains(s)) {
											spell.append(py);
											firstSpell.append(py.charAt(0));
											flag = true;
											break;
										}
									}
								}

							}// end of for
							if (!flag) { // 都没有找到，匹配默认的 读音 大
								String py = spellArray[0];
								spell.append(py);
								firstSpell.append(py.charAt(0));
							}
						}
					} else {// 非汉字，无法转化成拼音
						firstSpell.append(charOfCN[i]);
						spell.append(charOfCN[i]);
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {

					return null;
				}

			} 
			else {//非中文字符直接append
				firstSpell.append(charOfCN[i]);
				spell.append(charOfCN[i]);
			}
		}
		return new String[] { firstSpell.toString(), spell.toString() };
	}

	public static void add2Index(Document doc, String firstPyFld,
			String wholePyFld, String value) {
		if (doc == null || value == null || value.isEmpty()) {
			return;
		}
		if (value.length() < value.getBytes().length) {
			String[] result = getFirstAndHanyuPinyin(value);

			if (result == null || result.length != 2) {
				return;
			}
			if (isValid(firstPyFld)) {
				Field field = new Field(firstPyFld, result[0], Store.NO,
						Index.NOT_ANALYZED);
				doc.add(field);
			}
			if (isValid(wholePyFld)) {
				Field field = new Field(wholePyFld, result[1], Store.NO,
						Index.NOT_ANALYZED);
				doc.add(field);
			}
		}

	}



	public static BooleanQuery getPyQuery(String firstPyFld, String wholePyFld,
			String value) {
		if (!isValid(value)) {
			return null;
		}
		if (value.length() == value.getBytes().length) {// 输入的全是英文才会进入拼音搜索,这时keyword是全英文字符
			BooleanQuery query = new BooleanQuery();
			BooleanQuery pinyinQuery = new BooleanQuery();
			if (isValid(firstPyFld)) {
				TermQuery firstQuery = new TermQuery(
						new Term(firstPyFld, value));
				pinyinQuery.add(firstQuery, Occur.SHOULD);
			}
			if (isValid(wholePyFld)) {
				TermQuery wholeQuery = new TermQuery(
						new Term(wholePyFld, value));
				pinyinQuery.add(wholeQuery, Occur.SHOULD);
			}

			if (pinyinQuery.getClauses().length > 0) {
				// pinyinQuery.setMinimumNumberShouldMatch(1);
				query.add(pinyinQuery, Occur.SHOULD);
			}
			return query;
		} else {
			return null;
		}
	}

	private static boolean isValid(String string) {
		if (string == null || string.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public static String getKeywordByPinyin(String pinyin) {
		String result = null;
		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(
					new File(BuildIndex.INDEX_PATH)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Query query = new TermQuery(new Term("pinyin", pinyin));
			TopDocs docs = searcher.search(query, 1);
			if (docs.totalHits > 0) {
				Document doc = reader.document(docs.scoreDocs[0].doc);
				result = doc.get("keyword");
				return result;
			} else {
				return null;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static void main(String[] args) {
		String str = "长春市长java";
		String[] result = getFirstAndHanyuPinyin(str);
		System.out.println(result[0]);
		System.out.println(result[1]);
		
		System.out.println(getHanyuPinyin(str));

	}
}

package com.example.demo.LanguageUtil;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import org.springframework.util.StringUtils;

import java.util.List;

public class LanguageDemo {

    public static void main(String[] args) {
        System.out.println("首次编译运行时，HanLP会自动构建词典缓存，请稍候……\n");
        //第一次运行会有文件找不到的错误但不影响运行，缓存完成后就不会再有了
        System.out.println("标准分词：");
        System.out.println(HanLP.segment("你好，欢迎使用HanLP！"));
        System.out.println("\n");

        List<Term> termList = NLPTokenizer.segment("中国科学院计算技术研究所的宗成庆教授正在教授自然语言处理课程");
        System.out.println("NLP分词：");
        System.out.println(termList);
        System.out.println("\n");

        System.out.println("智能推荐：");
        getSegement();
        System.out.println("\n");

    }

    public static void distance(){
        String[] wordArray = new String[]
                {
                        "香蕉",
                        "苹果",
                        "白菜",
                        "水果",
                        "蔬菜",
                        "自行车",
                        "公交车",
                        "飞机",
                        "买",
                        "卖",
                        "购入",
                        "新年",
                        "春节",
                        "丢失",
                        "补办",
                        "办理",
                        "送给",
                        "寻找",
                        "孩子",
                        "教室",
                        "教师",
                        "会计",
                };
        for (String a : wordArray)
        {
            for (String b : wordArray)
            {
                System.out.println(a + "\t" + b + "\t之间的距离是\t" + CoreSynonymDictionary.distance(a, b));
            }
        }

    }
    /**
     * 智能推荐部分
     */
    public static void getSegement() {
        Suggester suggester = new Suggester();
        String[] titleArray = ("威廉王子发表演说 呼吁保护野生动物\n" + "《时代》年度人物最终入围名单出炉 普京马云入选\n" + "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n"
                + "日本保密法将正式生效 日媒指其损害国民知情权\n" + "英报告说空气污染带来“公共健康危机”").split("\\n");
        for (String title : titleArray) {
            suggester.addSentence(title);
        }
        System.out.println(suggester.suggest("发言", 1)); // 语义
        System.out.println(suggester.suggest("危机公共", 1)); // 字符
        System.out.println(suggester.suggest("mayun", 1)); // 拼音
    }

    /**
     * 关键字提取
     */
    public static List<String> getMainIdea(String content,int size) {
        if (StringUtils.isEmpty(content) || size < 0){
            return null;
        }
        return HanLP.extractKeyword(content, size);
    }

    /**
     * 自动摘要
     * @param content 需要提取摘要的内容
     * @param size 需要提交摘要的的个数
     * @return 摘要的集合
     */
    public static List<String> getZhaiYao(String content,int size) {
        if (StringUtils.isEmpty(content)){
            return null;
        }
        return HanLP.extractSummary(content, size);
    }

    /**
     * 短语提取
     * @param content 需要提取的内容
     * @param size 需要提取短语短语个数
     * @return 提取的短语的集合
     */
    public static List<String> getDuanYu(String content,int size) {
        if (StringUtils.isEmpty(content) || size < 0){
            return null;
        }
        return HanLP.extractPhrase(content, size);
    }
}


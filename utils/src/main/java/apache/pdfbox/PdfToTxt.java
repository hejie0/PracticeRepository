/*
package apache.pdfbox;

import com.alibaba.fastjson.parser.ParseContext;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.net.MalformedURLException;


public class PdfToTxt {

    public static void main(String[] args) {
        try {
            //取得F盘下的pdf的内容
            //readPdf("D:/baiduyun/BaiduNetdiskDownload/Zookeeper.pdf");
            //readPdf("D:/baiduyun/BaiduNetdiskDownload/[黑天鹅]纳西姆·尼古拉斯·塔勒布.pdf");
            READPDF("D:/baiduyun/BaiduNetdiskDownload/算法之道.pdf");
            readPDF("D:/baiduyun/BaiduNetdiskDownload/算法之道.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(System.getProperty("org.apache.pdfbox.baseParser.pushBackSize"));
    }

    */
/**
     * 传入一个.pdf文件
     *
     * @param filePathName
     * @throws Exception
     *//*

    public static void readPdf(String filePathName) throws Exception {
        // 是否排序
        boolean sort = false;
        // pdf文件名
        File pdfFile1 = new File(filePathName);
        // 输入文本文件名称
        String targetFile = "D:/baiduyun/BaiduNetdiskDownload/";
        // 编码方式
        String encoding = "UTF-8";
        // 开始提取页数
        int startPage = 1;
        // 结束提取页数
        int endPage = Integer.MAX_VALUE;
        // 文件输入流，生成文本文件
        Writer output = null;
        // 内存中存储的PDF Document
        PDDocument document = null;
        try {
            try {
                //注意参数已不是以前版本中的URL.而是File。
                document = PDDocument.load(new FileInputStream(pdfFile1));
                // 获取PDF的文件名
                String fileName = pdfFile1.getName();
                // 以原来PDF的名称来命名新产生的txt文件
                if (fileName.length() > 4) {
                    targetFile += fileName.substring(0, fileName.length() - 4) + ".txt";
                }
            } catch (MalformedURLException e) {
                // 如果作为URL装载得到异常则从文件系统装载
                //注意参数已不是以前版本中的URL.而是File。
                document = PDDocument.load(pdfFile1);
                if (filePathName.length() > 4) {
                    targetFile = filePathName.substring(0, filePathName.length() - 4) + ".txt";
                }
            }
            // 文件输入流，写入文件倒textFile
            output = new OutputStreamWriter(new FileOutputStream(targetFile), encoding);
            // PDFTextStripper来提取文本
            PDFTextStripper stripper = null;
            stripper = new PDFTextStripper();
            // 设置是否排序
            stripper.setSortByPosition(sort);
            // 设置起始页
            stripper.setStartPage(startPage);
            // 设置结束页
            stripper.setEndPage(endPage);
            // 调用PDFTextStripper的writeText提取并输出文本
            stripper.writeText(document, output);

            System.out.println(targetFile + " 输出成功！");
        } finally {
            if (output != null) {
                // 关闭输出流
                output.close();
            }
            if (document != null) {
                // 关闭PDF Document
                document.close();
            }
        }
    }


    */
/**
     * 读取pdf中文字信息(全部)
     *//*

    public static void READPDF(String inputFile) {
        //创建文档对象
        PDDocument doc = null;
        String content = "";
        try {
            //加载一个pdf对象
            doc = PDDocument.load(new File(inputFile));
            //获取一个PDFTextStripper文本剥离对象
            PDFTextStripper textStripper = new PDFTextStripper();
            content = textStripper.getText(doc);
            System.out.println("内容:" + content);
            System.out.println("全部页数" + doc.getNumberOfPages());
            //关闭文档
            doc.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    */
/**
     * 读PDF文件，使用了pdfbox开源项目
     *
     * @param fileName
     *//*

    public static void readPDF(String fileName) {
        File file = new File(fileName);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            // 新建一个PDF解析器对象
            PDFParser parser = new PDFParser(in);
            // 对PDF文件进行解析
            parser.parse();
            // 获取解析后得到的PDF文档对象
            PDDocument pdfdocument = parser.getPDDocument();
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 从PDF文档对象中剥离文本
            String result = stripper.getText(pdfdocument);
            System.out.println("PDF文件的文本内容如下：");
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("读取PDF文件" + file.getAbsolutePath() + "生失败！" + e);
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    public static String parsePdf() {

        try {

            BodyContentHandler handler = new BodyContentHandler();

            Metadata metadata = new Metadata();

            FileInputStream inputstream = new FileInputStream(new File("D:\\1111.pdf"));

            ParseContext pcontext = new ParseContext();


            //parsing the document using PDF parser

            PDFParser pdfparser = new PDFParser();

            pdfparser.parse(inputstream, handler, metadata, pcontext);

            //getting the content of the document

            System.out.println("Contents of the PDF :" + handler.toString());

            // 元数据提取

            System.out.println("Metadata of the PDF:");

            String[] metadataNames = metadata.names();

            for (String name : metadataNames) {

                System.out.println(name + " : " + metadata.get(name));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }
        return "";
    }

    public static void printPDF(String filePathName){
        //选择要提取的文件
        File file = new File(filePathName);

        //获取并打印文件内容
        Tika tika = new Tika();
        String filecontent = tika.parseToString(file);
        System.out.println("Extracted Content: " + filecontent);
    }
}
*/

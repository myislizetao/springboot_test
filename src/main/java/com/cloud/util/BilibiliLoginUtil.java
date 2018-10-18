package com.cloud.util;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * B站模拟登录类(只是单纯的实现滑动验证码模拟)
 */
public class BilibiliLoginUtil {

    private static String linkUrl = "https://passport.bilibili.com/login";//请求的链接
    private static String basePath = "F:/immm/";//根路径
    private static String full_image_name = "full-image";//原始图名称
    private static String bg_image_name = "bg-image";//带背景的图片名称
//    private static int[][] moveArray = new int[52][2];//52组图片移动的x轴跟y轴坐标
    private static int[][] fullMoveArray = new int[52][2];
    private static int[][] bgmoveArray = new int[52][2];
    private static boolean moveArrayInit = false;

    public static void main(String[] args) {
        invoke();
    }

    private static WebDriver driver;

    static {
        System.setProperty("webdriver.firefox.bin", "F:/Mozilla Firefox/firefox.exe");
        System.setProperty("webdriver.gecko.driver", "C:/Users/Administrator/Downloads/geckodriver-v0.16.0-win64/geckodriver.exe");
//        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
//            System.setProperty("webdriver.chrome.driver", "/Users/wangyang/workspace/selenium/chromedriver_V2.30/chromedriver");
//        }
        driver = new FirefoxDriver();
    }

    public static void invoke(){
        try{
            driver.get(linkUrl);
            By movenBtn = By.cssSelector(".gt_slider_knob.gt_show");
            waitForLoad(driver,movenBtn);
            WebElement moveElemet = driver.findElement(movenBtn);
            moveElemet.click();
//            move(driver,moveElemet,5);
            Integer distance = getMoveDistance(driver);
            move(driver, moveElemet, distance - 14);
            By gtTypeBy = By.cssSelector(".gt_info_type");
            By gtInfoBy = By.cssSelector(".gt_info_content");
            waitForLoad(driver, gtTypeBy);
            waitForLoad(driver, gtInfoBy);
            String gtType = driver.findElement(gtTypeBy).getText();
            String gtInfo = driver.findElement(gtInfoBy).getText();
            System.out.println(gtType + "---" + gtInfo+"-----移动的步子:"+distance);
/**
 * 再来一次：
 * 验证失败：
 */
            if (!gtType.equals("再来一次:") && !gtType.equals("验证失败:")) {
                Thread.sleep(4000);
                System.out.println(driver);
                return;
            }
            Thread.sleep(4000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 等待元素加载，10s超时
     * @param driver
     * @param by
     */
    public static void waitForLoad(WebDriver driver,By by){
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement element = driver.findElement(by);
                if (element != null) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 计算需要平移的距离x
     * @param driver
     * @return
     */
    public static int getMoveDistance(WebDriver driver) throws Exception {
        //获取页面资源
        String pageSource = driver.getPageSource();
        //获取原始图片
        String fullImageUrl = getFullImageUrl(pageSource);
        //把原始图片从网络上存到本地
        FileUtils.copyURLToFile(new URL(fullImageUrl), new File(basePath + full_image_name + ".jpg"));
        //获取带背景的图片url
        String bgImageUrl = getBgImageUrl(pageSource);
        //把带背景的图片从网络上存到本地
        FileUtils.copyURLToFile(new URL(bgImageUrl), new File(basePath + bg_image_name + ".jpg"));
        //获取带背景的图片的 background-position的所有x跟y轴坐标
        initMoveArray(driver,1);
        initMoveArray(driver,0);
        //还原图片
        restoreImage(full_image_name,0);
        restoreImage(bg_image_name,1);
        //获取这两张图片
        BufferedImage fullBI = ImageIO.read(new File(basePath + "result/" + full_image_name + "result3.jpg"));
        BufferedImage bgBI = ImageIO.read(new File(basePath + "result/" + bg_image_name + "result3.jpg"));
        for (int i = 0; i < bgBI.getWidth(); i++) {
            for (int j = 0; j < bgBI.getHeight(); j++) {
                int[] fullRgb = new int[3];
                fullRgb[0] = (fullBI.getRGB(i, j) & 0xff0000) >> 16;
                fullRgb[1] = (fullBI.getRGB(i, j) & 0xff00) >> 8;
                fullRgb[2] = (fullBI.getRGB(i, j) & 0xff);

                int[] bgRgb = new int[3];
                bgRgb[0] = (bgBI.getRGB(i, j) & 0xff0000) >> 16;
                bgRgb[1] = (bgBI.getRGB(i, j) & 0xff00) >> 8;
                bgRgb[2] = (bgBI.getRGB(i, j) & 0xff);
                if (difference(fullRgb, bgRgb) > 255) {
                    return i;
                }
            }
        }
        throw new RuntimeException("未找到需要平移的位置");
    }

    /**
     * 获取原始图片
     * @param pageSource
     * @return
     */
    private static String getFullImageUrl(String pageSource) {
        String url = null;
        Document document = Jsoup.parse(pageSource);
        String style = document.select("[class=gt_cut_fullbg_slice]").first().attr("style");
        Pattern pattern = Pattern.compile("url\\(\"(.*)\"\\)");
        Matcher matcher = pattern.matcher(style);
        if (matcher.find()) {
            url = matcher.group(1);
        }
        url = url.replace(".webp", ".jpg");
        System.out.println(url);
        return url;
    }

    /**
     * 获取带背景的url
     *
     * @param pageSource
     * @return
     */
    private static String getBgImageUrl(String pageSource) {
        String url = null;
        Document document = Jsoup.parse(pageSource);
        String style = document.select("[class=gt_cut_bg_slice]").first().attr("style");
        Pattern pattern = Pattern.compile("url\\(\"(.*)\"\\)");
        Matcher matcher = pattern.matcher(style);
        if (matcher.find()) {
            url = matcher.group(1);
        }
        url = url.replace(".webp", ".jpg");
        System.out.println(url);
        return url;
    }

    /**
     * 获取move数组
     *
     * @param driver
     */
    private static void initMoveArray(WebDriver driver,Integer flag) {
//        if (moveArrayInit) {
//            return;
//        }
        Document document = Jsoup.parse(driver.getPageSource());
        String cssQuery = "";
        if(flag==1){
            cssQuery = "[class=gt_cut_bg gt_show]";
        }else{
            cssQuery = "[class=gt_cut_fullbg gt_show]";
        }
        Elements elements = document.select(cssQuery).first().children();
        int i = 0;
        for (Element element : elements) {
            Pattern pattern = Pattern.compile(".*background-position: (.*?)px (.*?)px.*");
            Matcher matcher = pattern.matcher(element.toString());
            if (matcher.find()) {
                String width = matcher.group(1);
                String height = matcher.group(2);
                if(flag==1){
                    bgmoveArray[i][0] = Integer.parseInt(width);
                    bgmoveArray[i++][1] = Integer.parseInt(height);
                }else{
                    fullMoveArray[i][0] = Integer.parseInt(width);
                    fullMoveArray[i++][1] = Integer.parseInt(height);
                }
            } else {
                throw new RuntimeException("解析异常");
            }
        }
//        moveArrayInit = true;
    }

    /**
     * 还原图片
     *
     * @param type
     */
    private static void restoreImage(String type,Integer flag) throws IOException {
        //把图片裁剪为2 * 26份
        for (int i = 0; i < 52; i++) {
            if(flag==1){
                cutPic(basePath + type + ".jpg"
                        , basePath + "result/" + type + i + ".jpg", -bgmoveArray[i][0], -bgmoveArray[i][1], 10, 58);
            }else{
                cutPic(basePath + type + ".jpg"
                        , basePath + "result/" + type + i + ".jpg", -fullMoveArray[i][0], -fullMoveArray[i][1], 10, 58);
            }
        }
        //拼接图片
        String[] b = new String[26];
        for (int i = 0; i < 26; i++) {
            b[i] = String.format(basePath + "result/" + type + "%d.jpg", i);
        }
        mergeImage(b, 1, basePath + "result/" + type + "result1.jpg");
//拼接图片
        String[] c = new String[26];
        for (int i = 0; i < 26; i++) {
            c[i] = String.format(basePath + "result/" + type + "%d.jpg", i + 26);
        }
        mergeImage(c, 1, basePath + "result/" + type + "result2.jpg");
        mergeImage(new String[]{basePath + "result/" + type + "result1.jpg",
                basePath + "result/" + type + "result2.jpg"}, 2, basePath + "result/" + type + "result3.jpg");
//删除产生的中间图片
        for (int i = 0; i < 52; i++) {
            new File(basePath + "result/" + type + i + ".jpg").deleteOnExit();
        }
        new File(basePath + "result/" + type + "result1.jpg").deleteOnExit();
        new File(basePath + "result/" + type + "result2.jpg").deleteOnExit();
    }

    /**
     * 剪切图片
     * @param srcFile 原文件地址
     * @param outFile 新文件地址
     * @param x x轴坐标
     * @param y y轴坐标
     * @param width 宽
     * @param height 高
     * @return
     */
    public static boolean cutPic(String srcFile, String outFile, int x, int y,
                                 int width, int height) {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            if (!new File(srcFile).exists()) {
                return false;
            }
            is = new FileInputStream(srcFile);
            String ext = srcFile.substring(srcFile.lastIndexOf(".") + 1);
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(ext);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            File tempOutFile = new File(outFile);
            if (!tempOutFile.exists()) {
                tempOutFile.mkdirs();
            }
            ImageIO.write(bi, ext, new File(outFile));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (iis != null) {
                    iis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 图片拼接 （注意：必须两张图片长宽一致哦）
     *
     * @param files      要拼接的文件列表
     * @param type       1横向拼接，2 纵向拼接
     * @param targetFile 输出文件
     */
    private static void mergeImage(String[] files, int type, String targetFile) {
        int length = files.length;
        File[] src = new File[length];
        BufferedImage[] images = new BufferedImage[length];
        int[][] ImageArrays = new int[length][];
        for (int i = 0; i < length; i++) {
            try {
                src[i] = new File(files[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
        // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }
        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
        //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int difference(int[] a, int[] b) {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]);
    }

    /**
     * 移动
     *
     * @param driver
     * @param element
     * @param distance
     * @throws InterruptedException
     */
    public static void move(WebDriver driver, WebElement element, int distance) throws InterruptedException {
        int xDis = distance ;
        System.out.println("应平移距离：" + xDis);
        int moveX = new Random().nextInt(8) - 5;
        int moveY = 1;
        Actions actions = new Actions(driver);
        new Actions(driver).clickAndHold(element).perform();
        Thread.sleep(200);
        printLocation(element);
        actions.moveToElement(element, moveX, moveY).perform();
        System.out.println(moveX + "--" + moveY);
        printLocation(element);
        for (int i = 0; i < 22; i++) {
            int s = 10;
            if (i % 2 == 0) {
                s = -10;
            }
            actions.moveToElement(element, s, 1).perform();
// printLocation(element);
            Thread.sleep(new Random().nextInt(100) + 150);
        }

        System.out.println(xDis + "--" + 1);
        actions.moveByOffset(xDis, 1).perform();
        printLocation(element);
        Thread.sleep(200);
        actions.release(element).perform();
    }

    private static void printLocation(WebElement element) {
        org.openqa.selenium.Point point = element.getLocation();
        System.out.println(point.toString());
    }
}

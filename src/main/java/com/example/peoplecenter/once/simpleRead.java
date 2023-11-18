package com.example.peoplecenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 读取数据
 * @author lkj
 */

public class simpleRead {
    /**
     * 读取数据
     * @param args
     */
    public static void main(String[] args) {
        String fileName ="D:\\A-LKJwork\\项目\\伙伴匹配系统\\friend-match-backend\\src\\main\\java\\com\\example\\peoplecenter\\once\\data\\text.xlsx";
        System.out.println("监听器读：");
        readByListener(fileName);
        System.out.println("同步读：");
        synchronousRead(fileName);
    }

    /**
     * 监听器读
     * @param fileName
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName,XingqiuData.class,new DataListener()).sheet().doRead();
    }

    /**
     * 同步读
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        List<XingqiuData> list = EasyExcel.read(fileName).head(XingqiuData.class).sheet().doReadSync();
        for (XingqiuData xingqiuData:list){
            System.out.println(xingqiuData);
        }
    }
}
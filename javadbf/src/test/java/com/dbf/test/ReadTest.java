package com.dbf.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.gson.Gson;
import com.linuxense.javadbf.DBFField;
import com.xlzhang.dbf.ReadDBF;

public class ReadTest {

    public static void main(String[] args) {
        try {
            ReadDBF readDBF = new ReadDBF(new FileInputStream("D:\\test.dbf"),"UTF-8");
            readDBF.read();
            System.err.println(new Gson().toJson(readDBF.getRecords()));
            for (DBFField field : readDBF.getDBFFields()) {
                System.err.println(field.getDecimalCount());
                System.err.println("长度："+field.getFieldLength());
                System.err.println("列名："+field.getName());
                System.err.println("类型"+field.getDataType());
            }
            
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

package com.dbf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.linuxense.javadbf.DBFField;
import com.xlzhang.dbf.ReadDBF;
import com.xlzhang.dbf.WriteDBF;

/**
 * 功能: TODO.<br/>
 * date: 2016年6月17日 下午1:59:21 <br/>
 *
 * @author xlzhang@wisdombud.com
 * @version
 * @since JDK 1.7 Character C java.lang.String</br>
 *        Numeric N java.lang.Double</br>
 *        Double F lava.lang.Double</br>
 *        Logical L java.lang.Boolean</br>
 *        Date D java.util.Date</br>
 */
public class WriteTest {

	public static void main(String[] args) {

		List<DBFField> dbfFields = new ArrayList<DBFField>();
		DBFField ID = new DBFField();
		ID.setName("ID");
		ID.setDataType(DBFField.FIELD_TYPE_C);
		ID.setFieldLength(18);
		dbfFields.add(ID);

		DBFField DWDM = new DBFField();
		DWDM.setName("DWDM");
		DWDM.setDataType(DBFField.FIELD_TYPE_C);
		DWDM.setFieldLength(8);
		dbfFields.add(DWDM);

		DBFField KSFSDM = new DBFField();
		KSFSDM.setName("KSFSDM");
		KSFSDM.setDataType(DBFField.FIELD_TYPE_C);
		KSFSDM.setFieldLength(2);
		dbfFields.add(KSFSDM);

		DBFField YXSDM = new DBFField();
		YXSDM.setName("YXSDM");
		YXSDM.setDataType(DBFField.FIELD_TYPE_C);
		YXSDM.setFieldLength(3);
		dbfFields.add(YXSDM);

		DBFField YXSMC = new DBFField();
		YXSMC.setName("YXSMC");
		YXSMC.setDataType(DBFField.FIELD_TYPE_C);
		YXSMC.setFieldLength(100);
		dbfFields.add(YXSMC);

		DBFField ZYDM = new DBFField();
		ZYDM.setName("ZYDM");
		ZYDM.setDataType(DBFField.FIELD_TYPE_C);
		ZYDM.setFieldLength(6);
		dbfFields.add(ZYDM);

		DBFField ZYBZ = new DBFField();
		ZYBZ.setName("ZYBZ");
		ZYBZ.setDataType(DBFField.FIELD_TYPE_C);
		ZYBZ.setFieldLength(250);
		dbfFields.add(ZYBZ);

		DBFField YJFXDM = new DBFField();
		YJFXDM.setName("YJFXDM");
		YJFXDM.setDataType(DBFField.FIELD_TYPE_C);
		YJFXDM.setFieldLength(2);
		dbfFields.add(YJFXDM);

		DBFField YJFXMC = new DBFField();
		YJFXMC.setName("YJFXMC");
		YJFXMC.setDataType(DBFField.FIELD_TYPE_C);
		YJFXMC.setFieldLength(100);
		dbfFields.add(YJFXMC);

		DBFField ZDJS = new DBFField();
		ZDJS.setName("ZDJS");
		ZDJS.setDataType(DBFField.FIELD_TYPE_C);
		ZDJS.setFieldLength(250);
		dbfFields.add(ZDJS);

		DBFField NZSRS = new DBFField();
		NZSRS.setName("NZSRS");
		NZSRS.setDataType(DBFField.FIELD_TYPE_C);
		NZSRS.setFieldLength(10);
		dbfFields.add(NZSRS);

		DBFField ZYMC = new DBFField();
		ZYMC.setName("ZYMC");
		ZYMC.setDataType(DBFField.FIELD_TYPE_C);
		ZYMC.setFieldLength(100);
		dbfFields.add(ZYMC);
		List<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < 50; i++) {
			Test test = new Test(i + "", i + "", i + "", i + "", i + "", i + "", i + "", i + "", i + "", i + "", i + "",
					i + "");
			tests.add(test);
		}

		try {
			WriteDBF writeDBF = new WriteDBF(new FileOutputStream("D:/111.dbf"));
			writeDBF.buildFields(dbfFields).addRecords(tests).end();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("生成成功！");
		
		
		try {
			ReadDBF readDBF=new ReadDBF(new FileInputStream(new File("")));
			readDBF.read();
			System.out.println(readDBF.getDBFFields());
			System.out.println(readDBF.getRecords());
			readDBF.end();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}

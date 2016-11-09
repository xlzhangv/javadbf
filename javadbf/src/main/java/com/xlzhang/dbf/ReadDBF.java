package com.xlzhang.dbf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

public class ReadDBF {
	// 定义DBFWriter实例用来写DBF文件
	private DBFReader reader;
	private InputStream inputStream;

	private ReadDBF() {
	}

	public ReadDBF(InputStream fos) {
		try {
			this.inputStream = fos;
			reader = new DBFReader(this.inputStream);
			reader.setCharactersetName("UTF-8");
		} catch (DBFException e) {
			e.printStackTrace();
		}

	}

	public ReadDBF(InputStream fos, String chartset) {
		try {
			this.inputStream = fos;
			reader = new DBFReader(this.inputStream);
			reader.setCharactersetName(chartset);
		} catch (DBFException e) {
			e.printStackTrace();
		}

	}

	public void end() {
		// 写入数据
		try {
			this.dbfFields.clear();
			this.records.clear();
			this.inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<DBFField> dbfFields = new ArrayList<DBFField>();
	public List<Object[]> records = new ArrayList<Object[]>();

	public void read() {
		try {
			// 调用DBFReader对实例方法得到path文件中字段的个数
			int fieldsCount = reader.getFieldCount();

			// 取出字段信息
			for (int i = 0; i < fieldsCount; i++) {
				DBFField field = reader.getField(i);
				dbfFields.add(field);
			}
			Object[] rowValues;
			// 一条条取出path文件中记录
			while ((rowValues = reader.nextRecord()) != null) {
				records.add(rowValues);
			}
		} catch (DBFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取字段
	 * 
	 * @return
	 */
	public List<DBFField> getDBFFields() {
		return this.dbfFields;
	}

	/**
	 * 获取行数据
	 * 
	 * @return
	 */
	public List<Object[]> getRecords() {
		return this.records;
	}

}
package com.xlzhang.dbf;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

public class WriteDBF {
	// 定义DBFWriter实例用来写DBF文件
	private DBFWriter writer;
	private OutputStream stream;

	private WriteDBF() {
	}

	public WriteDBF(OutputStream stream) {
		super();
		createWriteDBF(stream);
		this.stream = stream;
	}

	public WriteDBF(OutputStream stream, String chartset) {
		super();
		createWriteDBF(stream, chartset);
		this.stream = stream;
	}

	public void createWriteDBF(OutputStream fos) {
		this.stream = fos;
		writer = new DBFWriter();
		writer.setCharactersetName("UTF-8");
	}

	public void createWriteDBF(OutputStream fos, String chartset) {
		this.stream = fos;
		writer = new DBFWriter();
		writer.setCharactersetName(chartset);
	}

	public void end() {
		// 写入数据
		try {
			writer.write(stream);
			stream.flush();
			stream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Map<String, Integer> recordIndex = new HashMap<String, Integer>();

	public WriteDBF buildFields(List<DBFField> dbfFields) {
		recordIndex.clear();
		for (int i = 0; i < dbfFields.size(); i++) {
			recordIndex.put(dbfFields.get(i).getName(), i);
		}
		// 定义DBF文件字段
		DBFField[] fields = new DBFField[dbfFields.size()];
		try {
			// 把字段信息写入DBFWriter实例，即定义表结构
			writer.setFields(dbfFields.toArray(fields));
		} catch (DBFException e) {
			e.printStackTrace();
		}
		return this;
	}

	public WriteDBF addRecords(List values) {
		for (int i = 0; i < values.size(); i++) {
			final Class clazz = values.get(i).getClass();
			final Field[] fields = clazz.getDeclaredFields();
			Object[] rowData = new Object[fields.length];
			for (int j = 0; j < fields.length; j++) {
				fields[j].setAccessible(true);
				PropertyDescriptor pd = null;
				try {
					pd = new PropertyDescriptor(fields[j].getName(), clazz);
				} catch (IntrospectionException e) {
					e.printStackTrace();
				}
				rowData[recordIndex.get(fields[j].getName())] = getReflectValue(values.get(i), pd);
			}
			try {
				writer.addRecord(rowData);
			} catch (DBFException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this;
	}

	/**
	 * 功能: 反射通过get方法获取value�?.<br/>
	 * date: 2015�?1�?6�? 上午11:14:58 <br/>
	 * 
	 * @author xdwang@wisdombud.com
	 * @param voList
	 * @param i
	 * @param pd
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private <T> Object getReflectValue(final Object object, final PropertyDescriptor pd) {
		final Method getMethod = pd.getReadMethod();// 获得get方法

		Object getValue;
		try {
			getValue = getMethod.invoke(object);
			if (getValue == null) {
				return "";
			}
			return getValue;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 执行get方法返回�?个Object
		return "";
	}
}
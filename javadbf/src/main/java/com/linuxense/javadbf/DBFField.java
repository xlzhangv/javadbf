package com.linuxense.javadbf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DBFField
{
  public static final byte FIELD_TYPE_C = 67;
  public static final byte FIELD_TYPE_L = 76;
  public static final byte FIELD_TYPE_N = 78;
  public static final byte FIELD_TYPE_F = 70;
  public static final byte FIELD_TYPE_D = 68;
  public static final byte FIELD_TYPE_M = 77;
  byte[] fieldName = new byte[11];
  byte dataType;
  int reserv1;
  int fieldLength;
  byte decimalCount;
  short reserv2;
  byte workAreaId;
  short reserv3;
  byte setFieldsFlag;
  byte[] reserv4 = new byte[7];
  byte indexFieldFlag;
  int nameNullIndex = 0;

  protected static DBFField createField(DataInput in)
    throws IOException
  {
    DBFField field = new DBFField();

    byte t_byte = in.readByte();
    if (t_byte == 13)
    {
      return null;
    }

    in.readFully(field.fieldName, 1, 10);
    field.fieldName[0] = t_byte;

    for (int i = 0; i < field.fieldName.length; i++)
    {
      if (field.fieldName[i] != 0)
        continue;
      field.nameNullIndex = i;
      break;
    }

    field.dataType = in.readByte();
    field.reserv1 = Utils.readLittleEndianInt(in);
    field.fieldLength = in.readUnsignedByte();
    field.decimalCount = in.readByte();
    field.reserv2 = Utils.readLittleEndianShort(in);
    field.workAreaId = in.readByte();
    field.reserv2 = Utils.readLittleEndianShort(in);
    field.setFieldsFlag = in.readByte();
    in.readFully(field.reserv4);
    field.indexFieldFlag = in.readByte();

    return field;
  }

  protected void write(DataOutput out)
    throws IOException
  {
    out.write(this.fieldName);
    out.write(new byte[11 - this.fieldName.length]);

    out.writeByte(this.dataType);
    out.writeInt(0);
    out.writeByte(this.fieldLength);
    out.writeByte(this.decimalCount);
    out.writeShort(0);
    out.writeByte(0);
    out.writeShort(0);
    out.writeByte(0);
    out.write(new byte[7]);
    out.writeByte(0);
  }

  public String getName()
  {
    return new String(this.fieldName, 0, this.nameNullIndex);
  }

  public byte getDataType()
  {
    return this.dataType;
  }

  public int getFieldLength()
  {
    return this.fieldLength;
  }

  public int getDecimalCount()
  {
    return this.decimalCount;
  }

  /** @deprecated */
  public void setFieldName(String value)
  {
    setName(value);
  }

  public void setName(String value)
  {
    if (value == null)
    {
      throw new IllegalArgumentException("Field name cannot be null");
    }

    if ((value.length() == 0) || (value.length() > 10))
    {
      throw new IllegalArgumentException("Field name should be of length 0-10");
    }

    this.fieldName = value.getBytes();
    this.nameNullIndex = this.fieldName.length;
  }

  public void setDataType(byte value)
  {
    switch (value)
    {
    case 68:
      this.fieldLength = 8;
    case 67:
    case 70:
    case 76:
    case 77:
    case 78:
      this.dataType = value;
      break;
    case 69:
    case 71:
    case 72:
    case 73:
    case 74:
    case 75:
    default:
      throw new IllegalArgumentException("Unknown data type");
    }
  }

  public void setFieldLength(int value)
  {
    if (value <= 0)
    {
      throw new IllegalArgumentException("Field length should be a positive number");
    }

    if (this.dataType == 68)
    {
      throw new UnsupportedOperationException("Cannot do this on a Date field");
    }

    this.fieldLength = value;
  }

  public void setDecimalCount(int value)
  {
    if (value < 0)
    {
      throw new IllegalArgumentException("Decimal length should be a positive number");
    }

    if (value > this.fieldLength)
    {
      throw new IllegalArgumentException("Decimal length should be less than field length");
    }

    this.decimalCount = (byte)value;
  }
}

/* Location:           R:\ee\javadbf-0.4.0\
 * Qualified Name:     com.linuxense.javadbf.DBFField
 * JD-Core Version:    0.6.0
 */
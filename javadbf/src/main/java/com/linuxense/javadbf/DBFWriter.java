package com.linuxense.javadbf;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class DBFWriter extends DBFBase
{
  DBFHeader header;
  Vector v_records = new Vector();
  int recordCount = 0;
  RandomAccessFile raf = null;
  boolean appendMode = false;

  public DBFWriter()
  {
    this.header = new DBFHeader();
  }

  public DBFWriter(File dbfFile)
    throws DBFException
  {
    try
    {
      this.raf = new RandomAccessFile(dbfFile, "rw");

      if ((!dbfFile.exists()) || (dbfFile.length() == 0L))
      {
        this.header = new DBFHeader();
        return;
      }

      this.header = new DBFHeader();
      this.header.read(this.raf);

      this.raf.seek(this.raf.length() - 1L);
    }
    catch (FileNotFoundException e)
    {
      throw new DBFException("Specified file is not found. " + e.getMessage());
    }
    catch (IOException e)
    {
      throw new DBFException(e.getMessage() + " while reading header");
    }

    this.recordCount = this.header.numberOfRecords;
  }

  public void setFields(DBFField[] fields)
    throws DBFException
  {
    if (this.header.fieldArray != null)
    {
      throw new DBFException("Fields has already been set");
    }

    if ((fields == null) || (fields.length == 0))
    {
      throw new DBFException("Should have at least one field");
    }

    for (int i = 0; i < fields.length; i++)
    {
      if (fields[i] != null)
        continue;
      throw new DBFException("Field " + (i + 1) + " is null");
    }

    this.header.fieldArray = fields;
    try
    {
      if ((this.raf != null) && (this.raf.length() == 0L))
      {
        this.header.write(this.raf);
      }
    }
    catch (IOException e)
    {
      throw new DBFException("Error accesing file");
    }
  }

  public void addRecord(Object[] values)
    throws DBFException
  {
    if (this.header.fieldArray == null)
    {
      throw new DBFException("Fields should be set before adding records");
    }

    if (values == null)
    {
      throw new DBFException("Null cannot be added as row");
    }

    if (values.length != this.header.fieldArray.length)
    {
      throw new DBFException("Invalid record. Invalid number of fields in row");
    }

    for (int i = 0; i < this.header.fieldArray.length; i++)
    {
      if (values[i] == null)
      {
        continue;
      }

      switch (this.header.fieldArray[i].getDataType())
      {
      case 67:
        if ((values[i] instanceof String)) continue;
        throw new DBFException("Invalid value for field " + i);
      case 76:
        if ((values[i] instanceof Boolean)) continue;
        throw new DBFException("Invalid value for field " + i);
      case 78:
        if ((values[i] instanceof Double)) continue;
        throw new DBFException("Invalid value for field " + i);
      case 68:
        if ((values[i] instanceof Date)) continue;
        throw new DBFException("Invalid value for field " + i);
      case 70:
        if ((values[i] instanceof Double))
          continue;
        throw new DBFException("Invalid value for field " + i);
      case 69:
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
      case 77: }  } if (this.raf == null)
    {
      this.v_records.addElement(values);
    }
    else
    {
      try
      {
        writeRecord(this.raf, values);
        this.recordCount += 1;
      }
      catch (IOException e)
      {
        throw new DBFException("Error occured while writing record. " + e.getMessage());
      }
    }
  }

  public void write(OutputStream out)
    throws DBFException
  {
    try
    {
      if (this.raf == null)
      {
        DataOutputStream outStream = new DataOutputStream(out);

        this.header.numberOfRecords = this.v_records.size();
        this.header.write(outStream);

        int t_recCount = this.v_records.size();
        for (int i = 0; i < t_recCount; i++)
        {
          Object[] t_values = (Object[])this.v_records.elementAt(i);

          writeRecord(outStream, t_values);
        }

        outStream.write(26);
        outStream.flush();
      }
      else
      {
        this.header.numberOfRecords = this.recordCount;
        this.raf.seek(0L);
        this.header.write(this.raf);
        this.raf.seek(this.raf.length());
        this.raf.writeByte(26);
        this.raf.close();
      }

    }
    catch (IOException e)
    {
      throw new DBFException(e.getMessage());
    }
  }

  public void write()
    throws DBFException
  {
    write(null);
  }

  private void writeRecord(DataOutput dataOutput, Object[] objectArray)
    throws IOException
  {
    dataOutput.write(32);
    for (int j = 0; j < this.header.fieldArray.length; j++)
    {
      switch (this.header.fieldArray[j].getDataType())
      {
      case 67:
        if (objectArray[j] != null)
        {
          String str_value = objectArray[j].toString();
          dataOutput.write(Utils.textPadding(str_value, this.characterSetName, this.header.fieldArray[j].getFieldLength()));
        }
        else
        {
          dataOutput.write(Utils.textPadding("", this.characterSetName, this.header.fieldArray[j].getFieldLength()));
        }

        break;
      case 68:
        if (objectArray[j] != null)
        {
          GregorianCalendar calendar = new GregorianCalendar();
          calendar.setTime((Date)objectArray[j]);
          StringBuffer t_sb = new StringBuffer();
          dataOutput.write(String.valueOf(calendar.get(1)).getBytes());
          dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(2) + 1), this.characterSetName, 2, 12, (byte) 48));
          dataOutput.write(Utils.textPadding(String.valueOf(calendar.get(5)), this.characterSetName, 2, 12, (byte) 48));
        }
        else
        {
          dataOutput.write("        ".getBytes());
        }

        break;
      case 70:
        if (objectArray[j] != null)
        {
          dataOutput.write(Utils.doubleFormating((Double)objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
        }
        else
        {
          dataOutput.write(Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), 12));
        }

        break;
      case 78:
        if (objectArray[j] != null)
        {
          dataOutput.write(Utils.doubleFormating((Double)objectArray[j], this.characterSetName, this.header.fieldArray[j].getFieldLength(), this.header.fieldArray[j].getDecimalCount()));
        }
        else
        {
          dataOutput.write(Utils.textPadding("?", this.characterSetName, this.header.fieldArray[j].getFieldLength(), 12));
        }

        break;
      case 76:
        if (objectArray[j] != null)
        {
          if ((Boolean)objectArray[j] == Boolean.TRUE)
          {
            dataOutput.write(84);
          }
          else
          {
            dataOutput.write(70);
          }
        }
        else
        {
          dataOutput.write(63);
        }

        break;
      case 77:
        break;
      case 69:
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
      default:
        throw new DBFException("Unknown field type " + this.header.fieldArray[j].getDataType());
      }
    }
  }
}

/* Location:           R:\ee\javadbf-0.4.0\
 * Qualified Name:     com.linuxense.javadbf.DBFWriter
 * JD-Core Version:    0.6.0
 */
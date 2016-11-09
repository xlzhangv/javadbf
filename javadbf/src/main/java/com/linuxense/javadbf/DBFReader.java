package com.linuxense.javadbf;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

public class DBFReader extends DBFBase {
    DataInputStream dataInputStream;
    DBFHeader header;
    boolean isClosed = true;

    public DBFReader(InputStream in) throws DBFException {
        try {
            this.dataInputStream = new DataInputStream(in);
            this.isClosed = false;
            this.header = new DBFHeader();
            this.header.read(this.dataInputStream);

            int t_dataStartIndex = this.header.headerLength - (32 + 32 * this.header.fieldArray.length) - 1;
            if (t_dataStartIndex > 0) {
                this.dataInputStream.skip(t_dataStartIndex);
            }
        } catch (IOException e) {
            throw new DBFException(e.getMessage());
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.header.year + "/" + this.header.month + "/" + this.header.day + "\n"
                                           + "Total records: " + this.header.numberOfRecords + "\nHEader length: "
                                           + this.header.headerLength + "");

        for (int i = 0; i < this.header.fieldArray.length; i++) {
            sb.append(this.header.fieldArray[i].getName());
            sb.append("\n");
        }

        return sb.toString();
    }

    public int getRecordCount() {
        return this.header.numberOfRecords;
    }

    public DBFField getField(int index) throws DBFException {
        if (this.isClosed) {
            throw new DBFException("Source is not open");
        }

        return this.header.fieldArray[index];
    }

    public int getFieldCount() throws DBFException {
        if (this.isClosed) {
            throw new DBFException("Source is not open");
        }

        if (this.header.fieldArray != null) {
            return this.header.fieldArray.length;
        }

        return -1;
    }

    public Object[] nextRecord() throws DBFException {
        if (this.isClosed) {
            throw new DBFException("Source is not open");
        }

        Object[] recordObjects = new Object[this.header.fieldArray.length];
        try {
            boolean isDeleted = false;
            do {
                if (isDeleted) {
                    this.dataInputStream.skip(this.header.recordLength - 1);
                }

                int t_byte = this.dataInputStream.readByte();
                if (t_byte == 26) {
                    return null;
                }

                isDeleted = t_byte == 42;
            } while (isDeleted);

            for (int i = 0; i < this.header.fieldArray.length; i++) {
                switch (this.header.fieldArray[i].getDataType()) {
                    case 67:
                        byte[] b_array = new byte[this.header.fieldArray[i].getFieldLength()];
                        this.dataInputStream.read(b_array);
                        recordObjects[i] = new String(b_array, this.characterSetName);
                        break;
                    case 68:
                        byte[] t_byte_year = new byte[4];
                        this.dataInputStream.read(t_byte_year);

                        byte[] t_byte_month = new byte[2];
                        this.dataInputStream.read(t_byte_month);

                        byte[] t_byte_day = new byte[2];
                        this.dataInputStream.read(t_byte_day);
                        try {
                            GregorianCalendar calendar =
                                new GregorianCalendar(Integer.parseInt(new String(t_byte_year)),
                                                      Integer.parseInt(new String(t_byte_month)) - 1,
                                                      Integer.parseInt(new String(t_byte_day)));

                            recordObjects[i] = calendar.getTime();
                        } catch (NumberFormatException e) {
                            recordObjects[i] = null;
                        }

                        break;
                    case 70:
                        try {
                            byte[] t_float = new byte[this.header.fieldArray[i].getFieldLength()];
                            this.dataInputStream.read(t_float);
                            t_float = Utils.trimLeftSpaces(t_float);
                            if ((t_float.length > 0) && (!Utils.contains(t_float, (byte) 63))) {
                                recordObjects[i] = new Float(new String(t_float));
                            } else {
                                recordObjects[i] = null;
                            }
                        } catch (NumberFormatException e) {
                            throw new DBFException("Failed to parse Float: " + e.getMessage());
                        }

                        break;
                    case 78:
                        try {
                            byte[] t_numeric = new byte[this.header.fieldArray[i].getFieldLength()];
                            this.dataInputStream.read(t_numeric);
                            t_numeric = Utils.trimLeftSpaces(t_numeric);

                            if ((t_numeric.length > 0) && (!Utils.contains(t_numeric, (byte) 63))) {
                                if(new String(t_numeric).length()==0){
                                    
                                    
                                }
                                recordObjects[i] = new Double(new String(t_numeric));
                            } else {
                                recordObjects[i] = null;
                            }
                        } catch (NumberFormatException e) {
                            throw new DBFException("Failed to parse Number: " + e.getMessage());
                        }

                        break;
                    case 76:
                        byte t_logical = this.dataInputStream.readByte();
                        if ((t_logical == 89) || (t_logical == 116) || (t_logical == 84) || (t_logical == 116)) {
                            recordObjects[i] = Boolean.TRUE;
                        } else {
                            recordObjects[i] = Boolean.FALSE;
                        }
                        break;
                    case 77:
                        recordObjects[i] = null;
                        
                        break;
                    case 69:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    default:
                        recordObjects[i] = null;
                }
            }
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            throw new DBFException(e.getMessage());
        }

        return recordObjects;
    }
}

/*
 * Location: R:\ee\javadbf-0.4.0\
 * Qualified Name: com.linuxense.javadbf.DBFReader
 * JD-Core Version: 0.6.0
 */
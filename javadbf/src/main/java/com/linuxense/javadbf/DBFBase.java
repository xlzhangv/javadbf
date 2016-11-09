package com.linuxense.javadbf;

public abstract class DBFBase
{
  protected String characterSetName = "8859_1";
  protected final int END_OF_DATA = 26;

  public String getCharactersetName()
  {
    return this.characterSetName;
  }

  public void setCharactersetName(String characterSetName)
  {
    this.characterSetName = characterSetName;
  }
}

/* Location:           R:\ee\javadbf-0.4.0\
 * Qualified Name:     com.linuxense.javadbf.DBFBase
 * JD-Core Version:    0.6.0
 */
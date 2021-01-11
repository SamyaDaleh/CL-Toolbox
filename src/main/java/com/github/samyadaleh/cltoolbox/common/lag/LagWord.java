package com.github.samyadaleh.cltoolbox.common.lag;

public class LagWord {
  private String wordSurface;
  private String[] category;

  public LagWord(String wordSurface, String[] wordCategory) {
    this.wordSurface = wordSurface;
    this.category = wordCategory;
  }

  public String getWordSurface() {
    return wordSurface;
  }

  public void setWordSurface(String wordSurface) {
    this.wordSurface = wordSurface;
  }

  public String[] getCategory() {
    return category;
  }

  public void setCategory(String[] category) {
    this.category = category;
  }
}

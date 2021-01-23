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

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("[").append(wordSurface).append(" ");
    if (category.length == 0) {
      repr.append("ε");
    } else {
      repr.append("(").append(String.join(" ", category)).append(")");
    }
    repr.append("]");
    return repr.toString();
  }
}

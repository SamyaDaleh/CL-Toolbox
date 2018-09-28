package com.github.samyadaleh.cltoolbox.gui;

import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParsingTraceTable {

  private List<DisplayTreeInterface> popups = new ArrayList<>();

  protected abstract DisplayTreeInterface generateItemPopup(int xCorrect,
      int yCorrect, String value);

  List<DisplayTreeInterface> getTreePopups() {
    for (int i = popups.size() - 1; i >= 0; i--) {
      popups.get(i).dispose();
      popups.remove(i);
    }
    String value = getHoverCellContent();
    if (value.charAt(0) == '[') {
      DisplayTreeInterface popup = generateItemPopup(0, 0, value);
      popups.add(popup);
    } else if (value.charAt(0) == '{') {
      Character[] specialChars = new Character[] {'{', '}', ','};
      TokenReader reader =
          new TokenReader(new StringReader(value), specialChars);
      Token token;
      int xCorrect = 0;
      int yCorrect = 0;
      while ((token = reader.getNextToken()) != null) {
        switch (token.getString()) {
        case "{":
          if (popups.size() > 0) {
            yCorrect -= popups.get(popups.size() - 1).getHeight() + 10;
          }
        case "}":
          xCorrect = 0;
          break;
        case ",":
          xCorrect += popups.get(popups.size() - 1).getWidth() + 10;
          break;
        default:
          DisplayTreeInterface popup =
              generateBackpointerPopup(token, xCorrect, yCorrect);
          popups.add(popup);
        }
      }
    }
    return popups;
  }

  protected abstract String getHoverCellContent();

  protected abstract DisplayTreeInterface generateBackpointerPopup(Token token,
      int xCorrect, int yCorrect);
}

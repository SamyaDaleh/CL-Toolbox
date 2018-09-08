package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a Buffered Reader as input and returns tokens as long as it either
 * has tokens stored or there are more lines in the reader to retrieve new
 * tokens from.
 */
public class TokenReader {
  private List<String> tokens = new ArrayList<>();
  private int lineNumber = 0;
  private final Character[] specialChars;
  private final BufferedReader in;

  public TokenReader(Reader reader, Character[] specialChars) {
    in = new BufferedReader(reader);
    this.specialChars= specialChars;
  }

  public Token getNextToken() throws IOException {
    String line = null;
    if(tokens.size() > 0 || (line = in.readLine()) != null) {
      if (tokens.size() == 0) {
        tokens = ArrayUtils.tokenize(line, specialChars);
        lineNumber++;
      }
      String token = tokens.get(0);
      tokens.remove(0);
      return new Token(token, lineNumber);
    }
    return null;
  }
}

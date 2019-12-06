package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  private static final Logger log = LogManager.getLogger();

  public TokenReader(Reader reader, Character[] specialChars) {
    in = new BufferedReader(reader);
    this.specialChars= specialChars;
  }

  public Token getNextToken() {
    String line = null;
    try {
      if(tokens.size() > 0 || (line = in.readLine()) != null) {
        if (tokens.size() == 0) {
          tokens = ArrayUtils.tokenize(line, specialChars);
          lineNumber++;
        }
        if (tokens.size() == 0) {
          return null;
        }
        String token = tokens.get(0);
        tokens.remove(0);
        return new Token(token, lineNumber);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
}

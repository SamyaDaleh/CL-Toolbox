package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.lag.Lag;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;

public class LagParser {

  /**
   * Hand CFG reader to parse from, can come from string or multiline file.
   */
  public static Lag parseLagReader(Reader reader)
      throws ParseException {
    BufferedReader in = new BufferedReader(reader);
    return new Lag(in);
  }

}

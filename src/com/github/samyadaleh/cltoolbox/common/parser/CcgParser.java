package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CcgParser {

  /**
   * Parses a CCG from a file and returns it as Pcfg.
   */
  public static Ccg parseCcgReader(Reader reader) throws IOException {
    BufferedReader in = new BufferedReader(reader);
    return new Ccg(in);
  }
}

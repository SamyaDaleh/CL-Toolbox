package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.*;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class GrammarLoader {

  public static Cfg readCfg(String fileName)
      throws FileNotFoundException, ParseException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/cfg/" + fileName));
    return CfgParser.parseCfgReader(grammarReader);
  }

  public static Pcfg readPcfg(String fileName)
      throws FileNotFoundException, ParseException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/pcfg/" + fileName));
    return PcfgParser.parsePcfgReader(grammarReader);
  }

  public static Tag readTag(String fileName)
      throws FileNotFoundException, ParseException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/tag/" + fileName));
    return TagParser.parseTagReader(grammarReader);
  }

  public static Srcg readSrcg(String fileName)
      throws FileNotFoundException, ParseException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/srcg/" + fileName));
    return SrcgParser.parseSrcgReader(grammarReader);
  }

  public static Ccg readCcg(String fileName) throws IOException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/ccg/" + fileName));
    return CcgParser.parseCcgReader(grammarReader);
  }

}

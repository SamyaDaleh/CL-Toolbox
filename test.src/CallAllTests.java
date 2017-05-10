import java.text.ParseException;

import astar.SxCalcTest;
import chartparsing.DeductionTest;
import common.ArrayUtilsTest;
import common.SetUtilsTest;
import common.cfg.CfgItemTest;
import common.tag.TreeTest;

public class CallAllTests {
  public static void main(String[] args) throws ParseException {
    SxCalcTest.main(new String[] {});
    DeductionTest.main(new String[] {});
    ArrayUtilsTest.main(new String[] {});
    SetUtilsTest.main(new String[] {});
    CfgItemTest.main(new String[] {});
    TreeTest.main(new String[] {});;
  }
}

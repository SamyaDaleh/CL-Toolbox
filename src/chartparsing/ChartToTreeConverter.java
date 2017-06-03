package chartparsing;

import common.tag.Tag;
import common.tag.Tree;

import java.util.ArrayList;
import java.util.List;

import common.Item;

/** Collection of functions that take a chart and a list of goal items and
 * return one tree that represents the successful parse. */
public class ChartToTreeConverter {

  public static Tree TagCykToDerivatedTree(List<Item> chart, List<Item> goals,
    ArrayList<ArrayList<String>> appliedRules,
    ArrayList<ArrayList<ArrayList<Integer>>> backPointers, Tag tag) {
    Tree derivationtree = null;
    for (Item goal : goals) {
      for (int i = 0; i < chart.size(); i++) {
        if (chart.get(i).equals(goal)) {
          ArrayList<String> steps = new ArrayList<String>();
          ArrayList<Integer> ids = new ArrayList<Integer>();
          ids.add(i);
          while (ids.size() > 0) {
            int currentid = ids.get(0);
            ids.remove(0);
            if (appliedRules.get(currentid).get(0).startsWith("adjoin")
              || appliedRules.get(currentid).get(0).startsWith("subst")) {
              steps.add(appliedRules.get(currentid).get(0));
            }
            ids.addAll(backPointers.get(currentid).get(0));
          }
          for (int j = steps.size()-1; j >= 0; j--) {
            String step = steps.get(j);
            String treename1 =
              step.substring(step.indexOf(" ") + 1, step.indexOf("["));
            String node1 =
              step.substring(step.indexOf("[") + 1, step.indexOf(","));
            if (node1.equals("ε")) {
              node1 = "";
            }
            String treename2 =
              step.substring(step.indexOf(",") + 1, step.indexOf("]"));
            if (step.startsWith("adjoin")) {
              if (j == steps.size()-1) {
                derivationtree = tag.getTree(treename1).adjoin(node1,
                  tag.getAuxiliaryTree(treename2));
              } else {
                derivationtree =
                    tag.getTree(treename1).adjoin(node1, derivationtree);
              }
            } else if (step.startsWith("subst")) {
              if (j == steps.size()-1) {
                derivationtree = tag.getTree(treename1).substitute(node1,
                  tag.getInitialTree(treename2));
              } else {
                derivationtree = tag.getTree(treename1).substitute(node1,
                  derivationtree );
              }
            }
          }
          return derivationtree;
        }
      }
    }
    return null;
  }
}

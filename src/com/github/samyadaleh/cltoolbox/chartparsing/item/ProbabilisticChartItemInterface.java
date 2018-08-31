package com.github.samyadaleh.cltoolbox.chartparsing.item;

/** Used for probabilistic parsing. it must be possible to retrieve the
 * probability or the weight of the item. */
public interface ProbabilisticChartItemInterface extends ChartItemInterface {

  Double getProbability();

}

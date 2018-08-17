package com.github.samyadaleh.cltoolbox.common;

/**
 * Util class for generating colors.
 */
class ColorUtils {

  /**
   * Returns an array of rgb integers for a color, which is determined by i,
   * while consequent colors vary across a spectrum of bright and clear colors
   * fitted to white backgrounds.
   */
  public static float[] getUniqueColor(int i) {
    double period1 = 30; // TODO test and adjust
    double period2 = goldenRatio(period1);
    double period3 = goldenRatio(period2);
    float value1 = (float) (28 * Math.sin((i * 2 * Math.PI) / period1) + 227);
    float value2 = (float) (50 * Math.sin((i * 2 * Math.PI) / period2) + 150);
    float value3 = (float) (77 * Math.sin((i * 2 * Math.PI) / period3) + 177);
    switch (i % 6) {
    case 1:
      return new float[] {value2, value1, value3};
    case 2:
      return new float[] {value3, value2, value1};
    case 3:
      return new float[] {value1, value3, value2};
    case 4:
      return new float[] {value3, value1, value2};
    case 5:
      return new float[] {value2, value3, value1};
    default:
      return new float[] {value1, value2, value3};
    }
  }

  private static double goldenRatio(double i) {
    return i * (1 + Math.sqrt(5)) / 2;
  }
}

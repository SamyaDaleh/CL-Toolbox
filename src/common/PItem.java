package common;

/** Used for probabilistic parsing. it must be possible to retrieve the
 * probability or the weight of the item. */
public interface PItem extends Item {

  Double getProbability();

}

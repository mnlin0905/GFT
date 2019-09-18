package com.linktech.gft.util

/**
 * function : 四个元素的对象
 *
 * Created on 2018/9/12  20:12
 * @author mnlin
 */
data class Quadra<A, B, C, D>(
        var first: A,
        var second: B,
        var third: C,
        var fourth: D
) {

    /**
     * Returns string representation of the [Triple] including its [first], [second] and [third] values.
     */
    override fun toString(): String = "($first, $second, $third,, $fourth)"

}

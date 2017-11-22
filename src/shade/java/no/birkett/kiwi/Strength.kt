package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
object Strength {

    /**
     * Used for required constraints
     */
    val REQUIRED = create(0b11111)

    /**
     * Used for optional user constraints
     */
    val STRONG = create(0b10000)

    /**
     * Used for implicit constraints
     */
    val IMPLICIT = create(0b01000)

    /**
     * Used for anything that should override existing defaults but not any manual constraints or implicit values
     */
    val PREFERRED = create(0b00100)

    /**
     * Used for width and height constraints so they will pull the left and bottom along with them
     */
    val MEDIUM = create(0b00010)

    /**
     * Used for constraints that keep anchors at their existing values
     */
    val WEAK = create(0b00001)

    private fun create(bitmask: Int): Double {
        var mask = bitmask
        var factor = 1.0
        var result = 0.0
        while(mask > 0) {
            if(mask and 1 == 1) {
                result += factor
            }
            factor *= 1000
            mask = mask shr 1
        }
        return result
    }

    fun clip(value: Double): Double {
        return Math.max(0.0, Math.min(REQUIRED, value))
    }
}

package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
enum class Strength(bitmask: Int) {

    /**
     * Used to represent no strength at all
     */
    NONE(0b00000),

    /**
     * Used for constraints that keep anchors at their existing values
     */
    WEAK(0b00001),

    /**
     * Used for width and height constraints so they will pull the left and bottom along with them
     */
    MEDIUM(0b00010),

    /**
     * Used for anything that should override existing defaults but not any manual constraints or implicit values
     */
    PREFERRED(0b00100),

    /**
     * Used for implicit constraints
     */
    IMPLICIT(0b01000),

    /**
     * Used for optional user constraints
     */
    STRONG(0b10000),

    /**
     * Used for required constraints
     */
    REQUIRED(0b11111);

    val value: Double = {
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
        result
    }()

    fun min(other: Strength): Strength {
        if(other < this) {
            return other
        }
        return this
    }

    fun max(other: Strength): Strength {
        if(other > this) {
            return other
        }
        return this
    }
}

package com.teamwizardry.librarianlib.common.util.math

class Vec2i(val x: Int, val y: Int) {

    @Transient val xf: Float
    @Transient val yf: Float

    init {
        this.xf = x.toFloat()
        this.yf = y.toFloat()
    }

    fun setX(value: Int): Vec2i {
        return Vec2i(value, y)
    }

    fun setY(value: Int): Vec2i {
        return Vec2i(x, value)
    }

    fun add(other: Vec2i): Vec2i {
        return Vec2i(x + other.x, y + other.y)
    }

    fun add(otherX: Int = 0, otherY: Int = 0): Vec2i {
        return Vec2i(x + otherX, y + otherY)
    }

    operator fun minus(other: Vec2i) = this.sub(other)
    fun sub(other: Vec2i): Vec2i {
        return Vec2i(x - other.x, y - other.y)
    }

    fun sub(otherX: Int, otherY: Int): Vec2i {
        return Vec2i(x - otherX, y - otherY)
    }

    fun mul(other: Vec2i): Vec2i {
        return Vec2i(x * other.x, y * other.y)
    }

    fun mul(otherX: Int, otherY: Int): Vec2i {
        return Vec2i(x * otherX, y * otherY)
    }

    fun mul(amount: Double): Vec2d {
        return Vec2d(x * amount, y * amount)
    }

    fun divide(other: Vec2i): Vec2d {
        return Vec2d(x / other.x.toDouble(), y / other.y.toDouble())
    }

    fun divide(otherX: Double, otherY: Double): Vec2d {
        return Vec2d(x / otherX, y / otherY)
    }

    fun divide(amount: Double): Vec2d {
        return Vec2d(x / amount, y / amount)
    }

    infix fun dot(point: Vec2i): Int {
        return x * point.x + y * point.y
    }

    @delegate:Transient
    private val len by lazy { Math.sqrt(x * x + y * y.toDouble()) }

    fun length(): Double {
        return len
    }

    fun normalize(): Vec2d {
        val norm = length()
        return Vec2d(x / norm, y / norm)
    }

    fun squareDist(vec: Vec2i): Int {
        val d0 = vec.x - x
        val d1 = vec.y - y
        return d0 * d0 + d1 * d1
    }


    //=============================================================================

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        var temp: Long
        temp = java.lang.Integer.toUnsignedLong(x)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Integer.toUnsignedLong(y)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        return x == (other as Vec2i).x && y == other.y
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    companion object {

        @JvmField val ZERO = Vec2d(0.0, 0.0)
        @JvmField val INFINITY = Vec2d(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
        @JvmField val NEG_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)

        @JvmField val ONE = Vec2d(1.0, 1.0)
        @JvmField val X = Vec2d(1.0, 0.0)
        @JvmField val Y = Vec2d(0.0, 1.0)
        @JvmField val X_INFINITY = Vec2d(Double.POSITIVE_INFINITY, 0.0)
        @JvmField val Y_INFINITY = Vec2d(0.0, Double.POSITIVE_INFINITY)

        @JvmField val NEG_ONE = Vec2d(-1.0, -1.0)
        @JvmField val NEG_X = Vec2d(-1.0, 0.0)
        @JvmField val NEG_Y = Vec2d(0.0, -1.0)
        @JvmField val NEG_X_INFINITY = Vec2d(Double.NEGATIVE_INFINITY, 0.0)
        @JvmField val NEG_Y_INFINITY = Vec2d(0.0, Double.NEGATIVE_INFINITY)

        @JvmStatic
        fun min(a: Vec2i, b: Vec2i): Vec2i {
            return Vec2i(Math.min(a.x, b.x), Math.min(a.y, b.y))
        }

        @JvmStatic
        fun max(a: Vec2i, b: Vec2i): Vec2i {
            return Vec2i(Math.max(a.x, b.x), Math.max(a.y, b.y))
        }
    }
}

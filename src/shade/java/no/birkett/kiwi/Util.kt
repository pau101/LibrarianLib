package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
object Util {
    private val EPS = 1.0e-8

    fun nearZero(value: Double): Boolean {
        return if (value < 0.0) -value < EPS else value < EPS
    }
}

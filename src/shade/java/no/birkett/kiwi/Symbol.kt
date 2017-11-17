package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
class Symbol @JvmOverloads constructor(val type: Type = Type.INVALID) {

    enum class Type {
        INVALID,
        EXTERNAL,
        SLACK,
        ERROR,
        DUMMY
    }

}

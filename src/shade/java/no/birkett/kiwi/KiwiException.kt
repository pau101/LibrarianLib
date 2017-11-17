package no.birkett.kiwi

/**
 * Created by alex on 30/01/16.
 */
open class KiwiException : Exception {
    constructor()
    constructor(message: String) : super(message)
}

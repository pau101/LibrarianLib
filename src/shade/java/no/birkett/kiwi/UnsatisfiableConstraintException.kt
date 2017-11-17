package no.birkett.kiwi

/**
 * Created by alex on 30/01/15.
 */
class UnsatisfiableConstraintException(private val constraint: Constraint) : KiwiException(constraint.toString())

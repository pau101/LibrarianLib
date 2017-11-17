package no.birkett.kiwi

import java.util.HashMap
import java.util.LinkedHashMap

/**
 * Created by alex on 30/01/15.
 */
class Row {

    var constant: Double = 0.0

    var cells: MutableMap<Symbol, Double> = LinkedHashMap()

    @JvmOverloads constructor(constant: Double = 0.0) {
        this.constant = constant
    }

    constructor(other: Row) {
        this.cells = LinkedHashMap(other.cells)
        this.constant = other.constant
    }

    /**
     * Add a constant value to the row constant.
     *
     * @return The new value of the constant
     */
    internal fun add(value: Double): Double {
        this.constant += value
        return this.constant
    }

    /**
     * Insert a symbol into the row with a given coefficient.
     *
     *
     * If the symbol already exists in the row, the coefficient will be
     * added to the existing coefficient. If the resulting coefficient
     * is zero, the symbol will be removed from the row
     */
    @JvmOverloads internal fun insert(symbol: Symbol, coefficient: Double = 1.0) {
        var coefficient = coefficient
        val existingCoefficient = cells[symbol]

        if (existingCoefficient != null) {
            coefficient += existingCoefficient
        }

        if (Util.nearZero(coefficient)) {
            cells.remove(symbol)
        } else {
            cells.put(symbol, java.lang.Double.valueOf(coefficient))
        }
    }

    /**
     * Insert a row into this row with a given coefficient.
     * The constant and the cells of the other row will be multiplied by
     * the coefficient and added to this row. Any cell with a resulting
     * coefficient of zero will be removed from the row.
     *
     * @param other
     * @param coefficient
     */
    @JvmOverloads internal fun insert(other: Row, coefficient: Double = 1.0) {
        this.constant += other.constant * coefficient

        for (s in other.cells.keys) {
            val coeff = other.cells[s]!! * coefficient

            //insert(s, coeff);  this line looks different than the c++

            //changes start here
            val value = this.cells[s]
            if (value == null) {
                this.cells.put(s, 0.0)
            }
            val temp = this.cells[s]!! + coeff
            this.cells.put(s, temp)
            if (Util.nearZero(temp)) {
                this.cells.remove(s)
            }
        }
    }

    /**
     * Remove the given symbol from the row.
     */
    internal fun remove(symbol: Symbol) {

        cells.remove(symbol)
        // not sure what this does, can the symbol be added more than once?
        /*CellMap::iterator it = m_cells.find( symbol );
        if( it != m_cells.end() )
            m_cells.erase( it );*/
    }

    /**
     * Reverse the sign of the constant and all cells in the row.
     */
    internal fun reverseSign() {
        this.constant = -this.constant

        val newCells = LinkedHashMap<Symbol, Double>()
        for (s in cells.keys) {
            val value = -cells[s]!!
            newCells.put(s, value)
        }
        this.cells = newCells
    }

    /**
     * Solve the row for the given symbol.
     *
     *
     * This method assumes the row is of the form a * x + b * y + c = 0
     * and (assuming solve for x) will modify the row to represent the
     * right hand side of x = -b/a * y - c / a. The target symbol will
     * be removed from the row, and the constant and other cells will
     * be multiplied by the negative inverse of the target coefficient.
     * The given symbol *must* exist in the row.
     *
     * @param symbol
     */
    internal fun solveFor(symbol: Symbol) {
        val coeff = -1.0 / cells[symbol]!!
        cells.remove(symbol)
        this.constant *= coeff

        val newCells = LinkedHashMap<Symbol, Double>()
        for (s in cells.keys) {
            val value = cells[s]!! * coeff
            newCells.put(s, value)
        }
        this.cells = newCells
    }

    /**
     * Solve the row for the given symbols.
     *
     *
     * This method assumes the row is of the form x = b * y + c and will
     * solve the row such that y = x / b - c / b. The rhs symbol will be
     * removed from the row, the lhs added, and the result divided by the
     * negative inverse of the rhs coefficient.
     * The lhs symbol *must not* exist in the row, and the rhs symbol
     * must* exist in the row.
     *
     * @param lhs
     * @param rhs
     */
    internal fun solveFor(lhs: Symbol, rhs: Symbol) {
        insert(lhs, -1.0)
        solveFor(rhs)
    }

    /**
     * Get the coefficient for the given symbol.
     *
     *
     * If the symbol does not exist in the row, zero will be returned.
     *
     * @return
     */
    internal fun coefficientFor(symbol: Symbol): Double {
        return if (this.cells.containsKey(symbol)) {
            this.cells[symbol]!!
        } else {
            0.0
        }
    }

    /**
     * Substitute a symbol with the data from another row.
     *
     *
     * Given a row of the form a * x + b and a substitution of the
     * form x = 3 * y + c the row will be updated to reflect the
     * expression 3 * a * y + a * c + b.
     * If the symbol does not exist in the row, this is a no-op.
     */
    internal fun substitute(symbol: Symbol, row: Row) {
        if (cells.containsKey(symbol)) {
            val coefficient = cells[symbol]!!
            cells.remove(symbol)
            insert(row, coefficient)
        }
    }

}
/**
 * Insert a symbol into the row with a given coefficient.
 *
 *
 * If the symbol already exists in the row, the coefficient will be
 * added to the existing coefficient. If the resulting coefficient
 * is zero, the symbol will be removed from the row
 */
/**
 * Insert a row into this row with a given coefficient.
 * The constant and the cells of the other row will be multiplied by
 * the coefficient and added to this row. Any cell with a resulting
 * coefficient of zero will be removed from the row.
 *
 * @param other
 */

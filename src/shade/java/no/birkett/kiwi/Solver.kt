package no.birkett.kiwi


import java.util.*

/**
 * Created by alex on 30/01/15.
 */
class Solver {

    data class Tag(var marker: Symbol = Symbol(), var other: Symbol = Symbol()) {
        fun copy() = Tag(marker, other)
    }

    data class EditInfo(var constraint: Constraint, var tag: Tag, var constant: Double) {
        fun copy() = EditInfo(constraint, tag.copy(), constant)
    }

    var changed = false

    private val cns = LinkedHashMap<Constraint, Tag>()
    private val rows = LinkedHashMap<Symbol, Row>()
    private val vars = LinkedHashMap<Variable, Symbol>()
    private val edits = LinkedHashMap<Variable, EditInfo>()
    private val infeasibleRows = ArrayList<Symbol>()
    private var objective = Row()
    private var artificial: Row? = null

    private val backup_cns = LinkedHashMap<Constraint, Tag>()
    private val backup_rows = LinkedHashMap<Symbol, Row>()
    private val backup_vars = LinkedHashMap<Variable, Symbol>()
    private val backup_infeasibleRows = ArrayList<Symbol>()
    private var backup_objective = Row()
    private var backup_artificial: Row? = null

    fun backup() {
        backup_cns.clear()
        backup_cns.putAll(cns.mapValues { it.value.copy() })
        backup_rows.clear()
        backup_rows.putAll(rows.mapValues { it.value.copy() })
        backup_vars.clear()
        backup_vars.putAll(vars)
        backup_infeasibleRows.clear()
        backup_infeasibleRows.addAll(infeasibleRows)
        backup_objective = objective.copy()
        backup_artificial = artificial?.copy()
    }

    fun restore() {
        cns.clear()
        cns.putAll(backup_cns)
        rows.clear()
        rows.putAll(backup_rows)
        vars.clear()
        vars.putAll(backup_vars)
        infeasibleRows.clear()
        infeasibleRows.addAll(backup_infeasibleRows)
        objective = backup_objective
        artificial = backup_artificial
    }

    /**
     * Add a constraint to the solver.
     *
     * @param constraint
     * @throws DuplicateConstraintException The given constraint has already been added to the solver.
     * @throws UnsatisfiableConstraintException      The given constraint is required and cannot be satisfied.
     */
    @Throws(DuplicateConstraintException::class, UnsatisfiableConstraintException::class)
    fun addConstraint(constraint: Constraint) {
        if (cns.containsKey(constraint)) {
            throw DuplicateConstraintException(constraint)
        }

        backup()

        val tag = Tag()
        val row = createRow(constraint, tag)
        var subject = chooseSubject(row, tag)

        if (subject.type == Symbol.Type.INVALID && allDummies(row)) {
            if (!Util.nearZero(row.constant)) {
                restore()
                throw UnsatisfiableConstraintException(constraint)
            } else {
                subject = tag.marker
            }
        }

        if (subject.type == Symbol.Type.INVALID) {
            if (!addWithArtificialVariable(row)) {
                restore()
                throw UnsatisfiableConstraintException(constraint)
            }
        } else {
            row.solveFor(subject)
            substitute(subject, row)
            this.rows.put(subject, row)
        }

        this.cns.put(constraint, tag)

        optimize(objective)
    }

    @Throws(UnknownConstraintException::class, InternalSolverError::class)
    fun removeConstraint(constraint: Constraint) {
        val tag = cns[constraint] ?: throw UnknownConstraintException(constraint)

        cns.remove(constraint)
        removeConstraintEffects(constraint, tag)

        var row: Row? = rows[tag.marker]
        if (row != null) {
            rows.remove(tag.marker)
        } else {
            row = getMarkerLeavingRow(tag.marker)
            if (row == null) {
                throw InternalSolverError("internal solver error")
            }

            //This looks wrong! changes made below
            //Symbol leaving = tag.marker;
            //rows.remove(tag.marker);

            var leaving: Symbol? = null
            for (s in rows.keys) {
                if (rows[s] === row) {
                    leaving = s
                }
            }
            if (leaving == null) {
                throw InternalSolverError("internal solver error")
            }

            rows.remove(leaving)
            row.solveFor(leaving, tag.marker)
            substitute(tag.marker, row)
        }
        optimize(objective)
    }

    internal fun removeConstraintEffects(constraint: Constraint, tag: Tag) {
        if (tag.marker.type == Symbol.Type.ERROR) {
            removeMarkerEffects(tag.marker, constraint.strength)
        } else if (tag.other.type == Symbol.Type.ERROR) {
            removeMarkerEffects(tag.other, constraint.strength)
        }
    }

    internal fun removeMarkerEffects(marker: Symbol, strength: Double) {
        val row = rows[marker]
        if (row != null) {
            objective.insert(row, -strength)
        } else {
            objective.insert(marker, -strength)
        }
    }

    internal fun getMarkerLeavingRow(marker: Symbol): Row? {
        val dmax = java.lang.Double.MAX_VALUE
        var r1 = dmax
        var r2 = dmax

        var first: Row? = null
        var second: Row? = null
        var third: Row? = null

        for (s in rows.keys) {
            val candidateRow = rows[s]!!
            val c = candidateRow.coefficientFor(marker)
            if (c == 0.0) {
                continue
            }
            if (s.type == Symbol.Type.EXTERNAL) {
                third = candidateRow
            } else if (c < 0.0) {
                val r = -candidateRow.constant / c
                if (r < r1) {
                    r1 = r
                    first = candidateRow
                }
            } else {
                val r = candidateRow.constant / c
                if (r < r2) {
                    r2 = r
                    second = candidateRow
                }
            }
        }

        if (first != null) {
            return first
        }
        return if (second != null) {
            second
        } else third
    }

    fun hasConstraint(constraint: Constraint): Boolean {
        return cns.containsKey(constraint)
    }

    @Throws(DuplicateEditVariableException::class, RequiredFailureException::class, UnsatisfiableConstraintException::class)
    fun addEditVariable(variable: Variable, strength: Double, value: Double) {
        var strength = strength
        if (edits.containsKey(variable)) {
            throw DuplicateEditVariableException()
        }

        strength = Strength.clip(strength)

//        if (strength == Strength.REQUIRED) {
//            throw RequiredFailureException()
//        }

        val constraint = Symbolics.equals(variable, value).setStrength(strength)

        addConstraint(constraint)

        val info = EditInfo(constraint, cns[constraint]!!, value)
        edits.put(variable, info)
        changed = true
    }

    @Throws(UnknownEditVariableException::class)
    fun removeEditVariable(variable: Variable) {
        val edit = edits[variable] ?: throw UnknownEditVariableException()

        try {
            removeConstraint(edit.constraint)
        } catch (e: UnknownConstraintException) {
            e.printStackTrace()
        }

        edits.remove(variable)
        changed = true
    }

    fun hasEditVariable(variable: Variable): Boolean {
        return edits.containsKey(variable)
    }

    fun editVariable(variable: Variable): EditInfo? {
        return edits[variable]
    }

    @Throws(UnknownEditVariableException::class)
    fun suggestValue(variable: Variable, value: Double) {
        val info = edits[variable] ?: throw UnknownEditVariableException()

        val delta = value - info.constant
        info.constant = value

        var row: Row? = rows[info.tag.marker]
        if (row != null) {
            if (row.add(-delta) < 0.0) {
                infeasibleRows.add(info.tag.marker)
            }
            dualOptimize()
            return
        }

        row = rows[info.tag.other]
        if (row != null) {
            if (row.add(delta) < 0.0) {
                infeasibleRows.add(info.tag.other)
            }
            dualOptimize()
            return
        }

        for (s in rows.keys) {
            val currentRow = rows[s]!!
            val coefficient = currentRow.coefficientFor(info.tag.marker)
            if (coefficient != 0.0 && currentRow.add(delta * coefficient) < 0.0 && s.type != Symbol.Type.EXTERNAL) {
                infeasibleRows.add(s)
            }
        }

        dualOptimize()
    }

    /**
     * Update the values of the external solver variables.
     */
    fun updateVariables() {

        for ((variable, value) in vars) {
            val row = this.rows[value]

            if (row == null) {
                variable.value = 0.0
            } else {
                variable.value = row.constant
            }
        }
    }


    /**
     * Create a new Row object for the given constraint.
     *
     *
     * The terms in the constraint will be converted to cells in the row.
     * Any term in the constraint with a coefficient of zero is ignored.
     * This method uses the `getVarSymbol` method to get the symbol for
     * the variables added to the row. If the symbol for a given cell
     * variable is basic, the cell variable will be substituted with the
     * basic row.
     *
     *
     * The necessary slack and error variables will be added to the row.
     * If the constant for the row is negative, the sign for the row
     * will be inverted so the constant becomes positive.
     *
     *
     * The tag will be updated with the marker and error symbols to use
     * for tracking the movement of the constraint in the tableau.
     */
    internal fun createRow(constraint: Constraint, tag: Tag): Row {
        val expression = constraint.expression
        val row = Row(expression.constant)

        for (term in expression.terms) {
            if (!Util.nearZero(term.coefficient)) {
                val symbol = getVarSymbol(term.variable)

                val otherRow = rows[symbol]

                if (otherRow == null) {
                    row.insert(symbol, term.coefficient)
                } else {
                    row.insert(otherRow, term.coefficient)
                }
            }
        }

        when (constraint.op) {
            RelationalOperator.OP_LE, RelationalOperator.OP_GE -> {
                val coeff = if (constraint.op == RelationalOperator.OP_LE) 1.0 else -1.0
                val slack = Symbol(Symbol.Type.SLACK)
                tag.marker = slack
                row.insert(slack, coeff)
                if (constraint.strength < Strength.REQUIRED) {
                    val error = Symbol(Symbol.Type.ERROR)
                    tag.other = error
                    row.insert(error, -coeff)
                    this.objective.insert(error, constraint.strength)
                }
            }
            RelationalOperator.OP_EQ -> {
                if (constraint.strength < Strength.REQUIRED) {
                    val errplus = Symbol(Symbol.Type.ERROR)
                    val errminus = Symbol(Symbol.Type.ERROR)
                    tag.marker = errplus
                    tag.other = errminus
                    row.insert(errplus, -1.0) // v = eplus - eminus
                    row.insert(errminus, 1.0) // v - eplus + eminus = 0
                    this.objective.insert(errplus, constraint.strength)
                    this.objective.insert(errminus, constraint.strength)
                } else {
                    val dummy = Symbol(Symbol.Type.DUMMY)
                    tag.marker = dummy
                    row.insert(dummy)
                }
            }
        }

        // Ensure the row as a positive constant.
        if (row.constant < 0.0) {
            row.reverseSign()
        }

        return row
    }

    /**
     * Choose the subject for solving for the row
     *
     *
     * This method will choose the best subject for using as the solve
     * target for the row. An invalid symbol will be returned if there
     * is no valid target.
     * The symbols are chosen according to the following precedence:
     * 1) The first symbol representing an external variable.
     * 2) A negative slack or error tag variable.
     * If a subject cannot be found, an invalid symbol will be returned.
     */
    private fun chooseSubject(row: Row, tag: Tag): Symbol {

        for ((key) in row.cells) {
            if (key.type == Symbol.Type.EXTERNAL) {
                return key
            }
        }
        if (tag.marker.type == Symbol.Type.SLACK || tag.marker.type == Symbol.Type.ERROR) {
            if (row.coefficientFor(tag.marker) < 0.0)
                return tag.marker
        }
        if (tag.other.type == Symbol.Type.SLACK || tag.other.type == Symbol.Type.ERROR) {
            if (row.coefficientFor(tag.other) < 0.0)
                return tag.other
        }
        return Symbol()
    }

    /**
     * Add the row to the tableau using an artificial variable.
     *
     *
     * This will return false if the constraint cannot be satisfied.
     */
    private fun addWithArtificialVariable(row: Row): Boolean {
        //TODO check this

        // Create and add the artificial variable to the tableau

        val art = Symbol(Symbol.Type.SLACK)
        rows.put(art, Row(row))

        this.artificial = Row(row)

        // Optimize the artificial objective. This is successful
        // only if the artificial objective is optimized to zero.
        optimize(this.artificial as Row)
        val success = Util.nearZero(artificial!!.constant)
        artificial = null

        // If the artificial variable is basic, pivot the row so that
        // it becomes basic. If the row is constant, exit early.

        val rowptr = this.rows[art]

        if (rowptr != null) {

            /**this looks wrong!!! */
            //rows.remove(rowptr);

            val deleteQueue = LinkedList<Symbol>()
            for (s in rows.keys) {
                if (rows[s] === rowptr) {
                    deleteQueue.add(s)
                }
            }
            while (!deleteQueue.isEmpty()) {
                val key = deleteQueue.pop()
            }
            deleteQueue.clear()

            if (rowptr.cells.isEmpty()) {
                return success
            }

            val entering = anyPivotableSymbol(rowptr)
            if (entering.type == Symbol.Type.INVALID) {
                return false // unsatisfiable (will this ever happen?)
            }
            rowptr.solveFor(art, entering)
            substitute(entering, rowptr)
            this.rows.put(entering, rowptr)
        }

        // Remove the artificial variable from the tableau.
        for ((_, value) in rows) {
            value.remove(art)
        }

        objective.remove(art)

        return success
    }

    /**
     * Substitute the parametric symbol with the given row.
     *
     *
     * This method will substitute all instances of the parametric symbol
     * in the tableau and the objective function with the given row.
     */
    internal fun substitute(symbol: Symbol, row: Row) {
        for ((key, value) in rows) {
            value.substitute(symbol, row)
            if (key.type != Symbol.Type.EXTERNAL && value.constant < 0.0) {
                infeasibleRows.add(key)
            }
        }

        objective.substitute(symbol, row)

        if (artificial != null) {
            artificial!!.substitute(symbol, row)
        }
    }

    /**
     * Optimize the system for the given objective function.
     *
     *
     * This method performs iterations of Phase 2 of the simplex method
     * until the objective function reaches a minimum.
     *
     * @throws InternalSolverError The value of the objective function is unbounded.
     */
    internal fun optimize(objective: Row) {
        changed = true
        while (true) {
            val entering = getEnteringSymbol(objective)
            if (entering.type == Symbol.Type.INVALID) {
                return
            }

            val entry = getLeavingRow(entering) ?: throw InternalSolverError("The objective is unbounded.")
            var leaving: Symbol? = null

            for (key in rows.keys) {
                if (rows[key] === entry) {
                    leaving = key
                }
            }

            var entryKey: Symbol? = null
            for (key in rows.keys) {
                if (rows[key] === entry) {
                    entryKey = key
                }
            }

            rows.remove(entryKey)
            entry.solveFor(leaving!!, entering)
            substitute(entering, entry)
            rows.put(entering, entry)
        }
    }

    @Throws(InternalSolverError::class)
    internal fun dualOptimize() {
        changed = true
        while (!infeasibleRows.isEmpty()) {
            val leaving = infeasibleRows.removeAt(infeasibleRows.size - 1)
            val row = rows[leaving]
            if (row != null && row.constant < 0.0) {
                val entering = getDualEnteringSymbol(row)
                if (entering.type == Symbol.Type.INVALID) {
                    throw InternalSolverError("internal solver error")
                }
                rows.remove(leaving)
                row.solveFor(leaving, entering)
                substitute(entering, row)
                rows.put(entering, row)
            }
        }
    }


    /**
     * Compute the entering variable for a pivot operation.
     *
     *
     * This method will return first symbol in the objective function which
     * is non-dummy and has a coefficient less than zero. If no symbol meets
     * the criteria, it means the objective function is at a minimum, and an
     * invalid symbol is returned.
     */
    private fun getEnteringSymbol(objective: Row): Symbol {

        for ((key, value) in objective.cells) {

            if (key.type != Symbol.Type.DUMMY && value < 0.0) {
                return key
            }
        }
        return Symbol()

    }

    private fun getDualEnteringSymbol(row: Row): Symbol {
        var entering = Symbol()
        var ratio = java.lang.Double.MAX_VALUE
        for (s in row.cells.keys) {
            if (s.type != Symbol.Type.DUMMY) {
                val currentCell = row.cells[s]!!
                if (currentCell > 0.0) {
                    val coefficient = objective.coefficientFor(s)
                    val r = coefficient / currentCell
                    if (r < ratio) {
                        ratio = r
                        entering = s
                    }
                }
            }
        }
        return entering
    }


    /**
     * Get the first Slack or Error symbol in the row.
     *
     *
     * If no such symbol is present, and Invalid symbol will be returned.
     */
    private fun anyPivotableSymbol(row: Row): Symbol {
        var symbol: Symbol? = null
        for ((key) in row.cells) {
            if (key.type == Symbol.Type.SLACK || key.type == Symbol.Type.ERROR) {
                symbol = key
            }
        }
        if (symbol == null) {
            symbol = Symbol()
        }
        return symbol
    }

    /**
     * Compute the row which holds the exit symbol for a pivot.
     *
     *
     * This documentation is copied from the C++ version and is outdated
     *
     *
     *
     *
     * This method will return an iterator to the row in the row map
     * which holds the exit symbol. If no appropriate exit symbol is
     * found, the end() iterator will be returned. This indicates that
     * the objective function is unbounded.
     */
    private fun getLeavingRow(entering: Symbol): Row? {
        var ratio = java.lang.Double.MAX_VALUE
        var row: Row? = null

        for (key in rows.keys) {
            if (key.type != Symbol.Type.EXTERNAL) {
                val candidateRow = rows[key]!!
                val temp = candidateRow.coefficientFor(entering)
                if (temp < 0) {
                    val temp_ratio = -candidateRow.constant / temp
                    if (temp_ratio < ratio) {
                        ratio = temp_ratio
                        row = candidateRow
                    }
                }
            }
        }
        return row
    }

    /**
     * Get the symbol for the given variable.
     *
     *
     * If a symbol does not exist for the variable, one will be created.
     */
    private fun getVarSymbol(variable: Variable): Symbol {
        val symbol: Symbol
        if (vars.containsKey(variable)) {
            symbol = vars[variable]!!
        } else {
            symbol = Symbol(Symbol.Type.EXTERNAL)
            vars.put(variable, symbol)
        }
        return symbol
    }

    /**
     * Test whether a row is composed of all dummy variables.
     */
    private fun allDummies(row: Row): Boolean {
        for ((key) in row.cells) {
            if (key.type != Symbol.Type.DUMMY) {
                return false
            }
        }
        return true
    }

}


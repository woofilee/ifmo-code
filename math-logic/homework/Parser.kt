/*
 * Nariman Safiulin (woofilee)
 * File: Parser.kt
 */

import java.util.*

/**
 * Class for expression parsers
 */
open class Parser {
    /**
     * Singleton for expression parsing
     */
    companion object {
        /**
         * Instance of the parser, for the static-behavior methods
         */
        private val instance = Parser()

        /**
         * Parses expression from the string
         *
         * @param  expression expression to parse
         * @return            parsed expression
         */
        fun single(expression: String) = instance.single(expression)

        /**
         * Parses expressions from the string
         *
         * @param  expressions expressions to parse
         * @return             parsed expressions
         */
        fun many(expressions: String) = instance.many(expressions)
    }

    /**
     * Parses expression from the string
     *
     * @param  expression expression to parse
     * @return            parsed expression
     */
    fun single(expression: String): Expression =
            Utils.clean(expression).let {
                parse(it, 0, it.length - 1)
            }

    /**
     * Parses expressions from the string
     *
     * @param  expressions expressions to parse
     * @return             parsed expressions
     */
    fun many(expressions: String): List<Expression> {
        val list = ArrayList<Expression>()
        var last = 0
        var weight: Int = 0

        /**
         * Checks the brackets balance and corrects this value if necessary
         */
        fun balance(pos: Int): Int = when (expressions[pos]) {
            '(' -> weight++
            ')' -> weight--
            else -> weight
        }

        for (pos in 0..expressions.length - 1) {
            if (weight == 0 && expressions[pos] == ',') {
                list.add(single(expressions.substring(last..pos - 1)))
                last = pos + 1
            }

            balance(pos)
        }

        list.add(single(expressions.substring(last..expressions.length - 1)))
        return list.toList()
    }

    /**
     * Parses expression from the string
     *
     * @param  expression expression to parse
     * @param  l          left bound of parsing
     * @param  r          right bound of parsing
     * @return            parsed expression
     */
    open protected fun parse(expression: String, l: Int, r: Int): Expression {
        var weight: Int = 0

        /**
         * Checks the brackets balance and corrects this value if necessary
         */
        fun balance(pos: Int): Int = when (expression[pos]) {
            '(' -> weight++
            ')' -> weight--
            else -> weight
        }

        // Implication
        for (pos in l..r) {
            if (weight == 0 && expression[pos] == '>')
                return parse(expression, l, pos - 2) as Logic impl
                        parse(expression, pos + 1, r) as Logic
            balance(pos)
        }

        // Disjunction
        for (pos in r downTo l) {
            if (weight == 0 && expression[pos] == '|')
                return parse(expression, l, pos - 1) as Logic disj
                        parse(expression, pos + 1, r) as Logic
            balance(pos)
        }

        // Conjunction
        for (pos in r downTo l) {
            if (weight == 0 && expression[pos] == '&')
                return parse(expression, l, pos - 1) as Logic conj
                        parse(expression, pos + 1, r) as Logic
            balance(pos)
        }

        // Negation
        if (expression[l] == '!')
            return !(parse(expression, l + 1, r) as Logic)

        // Universal or Existential quantifier check
        if (expression[l] == '@' || expression[l] == '?') {
            var m = l + 2
            while (Utils.isDigit(expression[m])) m++

            // Universal quantifier
            if (expression[l] == '@')
                return Variable(expression.substring(l + 1..m - 1)) all
                        parse(expression, m, r) as Logic

            // Existential quantifier
            return Variable(expression.substring(l + 1..m - 1)) exists
                    parse(expression, m, r) as Logic
        }

        // Predicate
        if (Utils.isUpperCase(expression[l])) {
            var m = l + 1
            while (m <= r && Utils.isDigit(expression[m])) m++

            if (m < r)
                return Predicate(expression.substring(l..m - 1), *(expression
                        .substring(m + 1..r - 1)
                        .let { many(it) }
                        .map { it as Math }
                        .toTypedArray()))
            else
                return Predicate(expression.substring(l..m - 1))
        }

        // Equals predicate
        for (pos in r downTo l) {
            if (weight == 0 && expression[pos] == '=')
                return parse(expression, l, pos - 1) as Math equals
                        parse(expression, pos + 1, r) as Math
            balance(pos)
        }

        // Addition binary operator
        for (pos in r downTo l) {
            if (weight == 0 && expression[pos] == '+')
                return parse(expression, l, pos - 1) as Math +
                        parse(expression, pos + 1, r) as Math
            balance(pos)
        }

        // Multiplication binary operator
        for (pos in r downTo l) {
            if (weight == 0 && expression[pos] == '*')
                return parse(expression, l, pos - 1) as Math *
                        parse(expression, pos + 1, r) as Math
            balance(pos)
        }

        // Function check
        if (Utils.isLowerCase(expression[l])) {
            var m = l + 1
            while (m <= r && Utils.isDigit(expression[m])) m++

            // Function
            if (m <= r && expression[m] == '(')
                return Function(expression.substring(l..m - 1), *(expression
                        .substring(m + 1..r - 1)
                        .let { many(it) }
                        .map { it as Math }
                        .toTypedArray()))
        }

        // Constant
        if (expression[r] == '\'')
            return Increment(parse(expression, l, r - 1) as Math)

        // Bracket expression
        if (expression[l] == '(')
            return parse(expression, l + 1, r - 1)

        // Constant
        if (expression[l] == '0')
            return Constant(0)

        // Variable // Anyway
        return Variable(expression.substring(l..r))
    }
}

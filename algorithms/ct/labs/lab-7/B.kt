/**
 * Nariman Safiulin (woofilee)
 * File: B.kt
 * Created on: May 24, 2016
 */

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.*

private val PROBLEM_NAME = "substr"

private class Scanner(file: File) {
    val br = BufferedReader(FileReader(file))
    var st = StringTokenizer("")

    fun hasNext(): Boolean {
        while (!st.hasMoreTokens()) {
            val line = br.readLine() ?: return false
            st = StringTokenizer(line)
        }
        return true
    }

    fun next(): String = if (hasNext()) st.nextToken() else ""
    fun nextInt(): Int = Integer.parseInt(next())
    fun nextLong(): Long = java.lang.Long.parseLong(next())
    fun nextDouble(): Double = java.lang.Double.parseDouble(next())
    fun close() = br.close()
}

private fun solve(`in`: Scanner, out: PrintWriter) {
    val t = `in`.nextInt()
    val r = `in`.nextInt()
    val s = `in`.next()
    val p = Array(s.length) { 1L }
    (1..s.length - 1).forEach { i -> p[i] = (p[i - 1] * t) % r }
    val h = Array(s.length) { i -> s[i].toLong() % r }
    (1..s.length - 1).forEach { i -> h[i] = (h[i] + (h[i - 1] * t) % r) % r }
    (1..`in`.nextInt()).forEach {
        val lb = `in`.nextInt()
        val rb = `in`.nextInt()
        out.println(if (lb == 0) h[rb] else (r + h[rb] - (h[lb - 1] * p[rb - lb + 1]) % r) % r)
    }
}

fun main(args: Array<String>) {
    val `in` = Scanner(File(PROBLEM_NAME + ".in"))
    val out = PrintWriter(File(PROBLEM_NAME + ".out"))

    solve(`in`, out)

    `in`.close()
    out.close()
}

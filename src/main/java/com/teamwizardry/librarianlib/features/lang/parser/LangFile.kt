package com.teamwizardry.librarianlib.features.lang.parser

import com.teamwizardry.librarianlib.features.lang.lexer.LangLexer
import java.io.InputStream
import java.io.InputStreamReader

class LangFile(stream: InputStream) {

    val duplicated: Map<String, List<LangPair>>
    val pairs: List<LangPair>

    init {
        val reader = InputStreamReader(stream)
        val lexer = LangLexer(reader)

        val root = LangBlock(lexer, null, "", null)
        root.parse()
        root.compute()

        val entries = root.collect()

        val grouped = entries.groupBy { it.key }

        duplicated = grouped.filter { (_, value) -> value.size > 1 }
        pairs = grouped.filter { (_, value) -> value.size == 1 }.values.map { it.first() }
    }
}

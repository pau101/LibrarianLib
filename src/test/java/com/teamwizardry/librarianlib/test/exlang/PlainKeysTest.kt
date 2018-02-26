package com.teamwizardry.librarianlib.test.exlang

object PlainKeysTest: ExLangTest() {
    override fun run() {
        test("Basic language keys") {
            "librarianlibtest.basic_key" % "The first basic key"
            "librarianlibtest.format_key" % "A key with a format code: %s"
            "librarianlibtest.newline_escape" % "A key with a newline\nescape"
        }
    }

}

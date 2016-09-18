package com.teamwizardry.librarianlib.client.font

import com.apple.laf.resources.aqua
import com.teamwizardry.librarianlib.LibrarianLog
import java.awt.Color
import java.awt.Font
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class GlyphLayout() {

    fun layout(str: String, format_: TextFormatting): List<GlyphDrawInfo> {
        var format = format_

        var x = 0f
        var y = 0f

        val list = ArrayList<GlyphDrawInfo>(str.length)

        var i = 0
        while(i < str.length) {
            val c = str[i]

            if(c == '§') {
                val len = validFormatLength(str, i+1)
                if(len == 0) {
                    i++ // increase once so i is at the next char. at the end of the loop i is incremented past it
                } else {
                    i++ // increase i so it's after the section sign
                    val formatStr = str.substring(i, i+len)
                    i += len

                    format = handleFormat(formatStr, format, format_)
                    continue
                }
            }

            val glyph = format.font.getGlyph(c.toInt())
            list.add(GlyphDrawInfo(x, y, glyph, format))
            x += ( glyph.metrics.advance * format.scale ).toInt()

            i++
        }

        return list
    }

    fun handleFormat(str: String, format_: TextFormatting, defaultFormat: TextFormatting): TextFormatting {
        if(str.length == 0) {
            LibrarianLog.warn("Somehow an empty format string got passed to GlyphLayout.handleFormat")
            return format_ // Why are we even here? I dunno, but I don't want to crash
        }
        var format = format_.clone()

        var singleChrs: String? = null
        if(str.length == 1) {
            singleChrs = str
        } else if(match(compoundExtract, str)) {
            singleChrs = lastMatchResult
        }

        if(singleChrs != null) {
            for(chr in singleChrs) {
                val s = Character.toString(chr)
                if(s in "kK") {
                    LibrarianLog.warn("Obfuscated text style isn't supported, and will never be. Who even uses it anyway?")
                } else

                if(s in "rR") {
                    format = defaultFormat.clone()
                } else

                if(s in "0123456789abcdefABCDEF") {
                    val i = Integer.parseInt(s, 16)

                    val bright = (i shr 3 and 1) * 85
                    var r = (i shr 2 and 1) * 170 + bright
                    val g = (i shr 1 and 1) * 170 + bright
                    val b = (i shr 0 and 1) * 170 + bright

                    if (i == 6) {
                        r += 85
                    }

                    format.text = Color(r and 255, g and 255, b and 255)
                } else
                if(s in "nN") {
                    format.underline = TextFormatting.DEFAULT
                } else
                if(s in "mM") {
                    format.strikethrough = TextFormatting.DEFAULT
                } else

                if(s in "oO") {
                    format.font = format.font.withStyle(format.font.getStyle() or Font.ITALIC)
                } else
                if(s in "lL") {
                    format.font = format.font.withStyle(format.font.getStyle() or Font.BOLD)
                }
            }
        }

        val color = parseColor(if(str.length < 3) "" else str.substring(1, str.length-1))
        if(color != null) {
            format.text = color
        }

        if(match(underlineRegex, str)) {
            format.underline = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(strikeRegex, str)) {
            format.strikethrough = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(shadowRegex, str)) {
            format.shadow = parseColor(lastMatchResult.toLowerCase())
        }
        if(match(styleRegex, str)) {
            lastMatchResult = lastMatchResult.toLowerCase()
            val i =
                    if(lastMatchResult == "plain")
                        Font.PLAIN
                    else if(lastMatchResult == "bold")
                        Font.BOLD
                    else if(lastMatchResult == "italic")
                        Font.ITALIC
                    else if(lastMatchResult == "bold_italic")
                        Font.BOLD or Font.ITALIC
                    else
                        Font.PLAIN
            format.font = format.font.withStyle(i)
        }
        if(match(fontRegex, str)) {
            val f = FontLoader.font(lastMatchResult, format.font.getStyle(), format.targetSize)
            format.font = f.first
            format.scale = f.second
        }

        return format
    }

    fun parseColor(str: String): Color? {
        if(str == "off")
            return null
        if(str == "on")
            return TextFormatting.DEFAULT

        val color = cssColors[str]
        var hex = if(str.startsWith("#")) str.substring(1) else str
        if(color != null)
            hex = color

        try {
            if(hex.length == 3)
                hex = "${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}"
            else if(hex.length == 4)
                hex = "${hex[0]}${hex[0]}${hex[1]}${hex[1]}${hex[2]}${hex[2]}${hex[3]}${hex[3]}"
            return Color(Integer.parseUnsignedInt(hex, 16), hex.length == 8)
        } catch (e: NumberFormatException) {
            return null
        }
    }

    var lastMatchResult = ""
    fun match(regex: Regex, str: String): Boolean {
        val match = regex.matchEntire(str)
        if(match == null) {
            lastMatchResult = ""
            return false
        } else {
            lastMatchResult = match.groups[1]?.value.toString()
            return true
        }
    }

    fun validFormatLength(str: String, i: Int): Int {
        if(i >= str.length)
            return 0
        val c = str[i]
        if(c in validOneCharFormats) {
            return 1
        }
        val sub = str.substring(i)
        val result = formatRegex.find(sub) ?: compoundRegex.find(sub)
        if(result != null) {
            return result.value.length
        }

        return 0
    }

    companion object {
        val validOneCharFormats = "0123456789abcdefABCDEFlmnorLMNOR"
        val formatRegex = Regex("^\\{.*?\\}")
        val compoundRegex = Regex("^\\[.*?\\]")
        val compoundExtract = Regex("\\[(\\w+)\\]")

        val underlineRegex = formatRegex("underline")
        val strikeRegex = formatRegex("strike")

        val shadowRegex = formatRegex("shadow")
        val styleRegex = formatRegex("style")
        val fontRegex = formatRegex("font")

        private fun formatRegex(str: String) = Regex("\\{$str=(.+)\\}")

        val cssColors: Map<String, String> by lazy {
            val l = listOf(
                "black","000000",
                "silver","c0c0c0",
                "gray","808080",
                "white","ffffff",
                "maroon","800000",
                "red","ff0000",
                "purple","800080",
                "fuchsia","ff00ff",
                "green","008000",
                "lime","00ff00",
                "olive","808000",
                "yellow","ffff00",
                "navy","000080",
                "blue","0000ff",
                "teal","008080",
                "aqua","00ffff",
                "orange","ffa500",
                "aliceblue","f0f8ff",
                "antiquewhite","faebd7",
                "aquamarine","7fffd4",
                "azure","f0ffff",
                "beige","f5f5dc",
                "bisque","ffe4c4",
                "blanchedalmond","ffebcd",
                "blueviolet","8a2be2",
                "brown","a52a2a",
                "burlywood","deb887",
                "cadetblue","5f9ea0",
                "chartreuse","7fff00",
                "chocolate","d2691e",
                "coral","ff7f50",
                "cornflowerblue","6495ed",
                "cornsilk","fff8dc",
                "crimson","dc143c",
                "darkblue","00008b",
                "darkcyan","008b8b",
                "darkgoldenrod","b8860b",
                "darkgray","a9a9a9",
                "darkgreen","006400",
                "darkgrey","a9a9a9",
                "darkkhaki","bdb76b",
                "darkmagenta","8b008b",
                "darkolivegreen","556b2f",
                "darkorange","ff8c00",
                "darkorchid","9932cc",
                "darkred","8b0000",
                "darksalmon","e9967a",
                "darkseagreen","8fbc8f",
                "darkslateblue","483d8b",
                "darkslategray","2f4f4f",
                "darkslategrey","2f4f4f",
                "darkturquoise","00ced1",
                "darkviolet","9400d3",
                "deeppink","ff1493",
                "deepskyblue","00bfff",
                "dimgray","696969",
                "dimgrey","696969",
                "dodgerblue","1e90ff",
                "firebrick","b22222",
                "floralwhite","fffaf0",
                "forestgreen","228b22",
                "gainsboro","dcdcdc",
                "ghostwhite","f8f8ff",
                "gold","ffd700",
                "goldenrod","daa520",
                "greenyellow","adff2f",
                "grey","808080",
                "honeydew","f0fff0",
                "hotpink","ff69b4",
                "indianred","cd5c5c",
                "indigo","4b0082",
                "ivory","fffff0",
                "khaki","f0e68c",
                "lavender","e6e6fa",
                "lavenderblush","fff0f5",
                "lawngreen","7cfc00",
                "lemonchiffon","fffacd",
                "lightblue","add8e6",
                "lightcoral","f08080",
                "lightcyan","e0ffff",
                "lightgoldenrodyellow","fafad2",
                "lightgray","d3d3d3",
                "lightgreen","90ee90",
                "lightgrey","d3d3d3",
                "lightpink","ffb6c1",
                "lightsalmon","ffa07a",
                "lightseagreen","20b2aa",
                "lightskyblue","87cefa",
                "lightslategray","778899",
                "lightslategrey","778899",
                "lightsteelblue","b0c4de",
                "lightyellow","ffffe0",
                "limegreen","32cd32",
                "linen","faf0e6",
                "mediumaquamarine","66cdaa",
                "mediumblue","0000cd",
                "mediumorchid","ba55d3",
                "mediumpurple","9370db",
                "mediumseagreen","3cb371",
                "mediumslateblue","7b68ee",
                "mediumspringgreen","00fa9a",
                "mediumturquoise","48d1cc",
                "mediumvioletred","c71585",
                "midnightblue","191970",
                "mintcream","f5fffa",
                "mistyrose","ffe4e1",
                "moccasin","ffe4b5",
                "navajowhite","ffdead",
                "oldlace","fdf5e6",
                "olivedrab","6b8e23",
                "orangered","ff4500",
                "orchid","da70d6",
                "palegoldenrod","eee8aa",
                "palegreen","98fb98",
                "paleturquoise","afeeee",
                "palevioletred","db7093",
                "papayawhip","ffefd5",
                "peachpuff","ffdab9",
                "peru","cd853f",
                "pink","ffc0cb",
                "plum","dda0dd",
                "powderblue","b0e0e6",
                "rosybrown","bc8f8f",
                "royalblue","4169e1",
                "saddlebrown","8b4513",
                "salmon","fa8072",
                "sandybrown","f4a460",
                "seagreen","2e8b57",
                "seashell","fff5ee",
                "sienna","a0522d",
                "skyblue","87ceeb",
                "slateblue","6a5acd",
                "slategray","708090",
                "slategrey","708090",
                "snow","fffafa",
                "springgreen","00ff7f",
                "steelblue","4682b4",
                "tan","d2b48c",
                "thistle","d8bfd8",
                "tomato","ff6347",
                "turquoise","40e0d0",
                "violet","ee82ee",
                "wheat","f5deb3",
                "whitesmoke","f5f5f5",
                "yellowgreen","9acd32",
                "rebeccapurple","663399"
            )
            val map = mutableMapOf<String, String>()
            for(i in 0..(l.size-1) step 2) {
                if(i+1 < l.size)
                    map[l[i]] = l[i+1]
            }
            return@lazy map
        }
    }

}

data class GlyphDrawInfo(val x: Float, val y: Float, val glyph: Glyph, val formatting: TextFormatting)

class TextFormatting protected constructor(var font: BasicFont, var targetSize: Int, var scale: Float, text: Color, shadow: Color? = null, underline: Color? = null, strikethrough: Color? = null) {
    constructor(fontName: String, style: Int, targetSize: Int) : this(FontLoader.font(fontName, style, targetSize).first, targetSize, FontLoader.font(fontName, style, targetSize).second, Color.BLACK)

    var text: Color = text
        set(value) {
            field = value
            darkerTextColor = Color(value.red/4, value.green/4, value.blue/4)
        }
    var darkerTextColor: Color = Color(text.red/4, text.green/4, text.blue/4)
        private set

    var shadow: Color? = shadow
        get() {
            if(field === DEFAULT) {
                return darkerTextColor
            }
            return field
        }

    var underline: Color? = underline
        get() {
            if(field === DEFAULT) {
                return text
            }
            return field
        }

    var strikethrough: Color? = strikethrough
        get() {
            if(field === DEFAULT) {
                return text
            }
            return field
        }

    fun clone(): TextFormatting {
        return TextFormatting(font, targetSize, scale, text, shadow, underline, strikethrough)
    }

    companion object {
        val DEFAULT = Color(0, 0, 0, 0)
    }
}

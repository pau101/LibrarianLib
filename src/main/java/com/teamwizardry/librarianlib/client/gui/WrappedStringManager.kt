package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class WrappedStringManager(val text: String, width_: Int, val fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRendererObj) {

    companion object {
        val NL = '\u200B'
        val SEP = "\u001E"
    }

    var width = width_
        set(value) {
            field = value
            rewrap()
        }
    var list: List<String>
    val fontHeight: Int

    init {
        WrappedStringManagerMethodHandles.get_FontRenderer_glyphWidth(fontRenderer)[NL.toInt()] = 0
        fontHeight = fontRenderer.FONT_HEIGHT
        list = wrapFormattedStringToWidth(text, width, SEP).split(SEP).map { it.replace('\n', NL) } // copied from rewrap
    }

    fun rewrap() {
        list = wrapFormattedStringToWidth(text, width, SEP).split(SEP).map { it.replace('\n', NL) }
    }

    @JvmOverloads
    fun draw(posX: Int, posY: Int, color: Color, dropShadow: Boolean = false) {
        for ((index, line) in list.withIndex()) {
            fontRenderer.drawString(line.filter { it != NL }, posX.toFloat(), posY.toFloat() + fontHeight * index, color.rgb, dropShadow)
        }
    }

    fun getIndexFromPos(x: Int, y: Int): Int {
        if (y < 0)
            return 0

        var lineY = 0
        var index = 0
        for (line in list) {
            if (y >= lineY && y <= lineY + fontHeight) {
                index += getIndexOnLine(line, x)
                break
            }
            index += line.length
            lineY += fontHeight
        }

        return index
    }


    fun getPosFromIndex(index: Int): Vec2d {
        val lineColumn = getLineAndColumnFromIndex(index)
        return Vec2d(fontRenderer.getStringWidth(list[lineColumn.yi].substring(0, lineColumn.xi)), lineColumn.yi * fontHeight)
    }

    fun getLineAndColumnFromIndex(index: Int): Vec2d {

        if (index < 0)
            return Vec2d.ZERO

        var yIndex = 0
        var currentIndex = 0
        for (line in list) {
            if (index == currentIndex + line.length && line.endsWith(NL))
                return Vec2d(0, yIndex + 1)

            if (index > currentIndex + line.length) {
                currentIndex += line.length
                yIndex++
                continue
            }

            var lineSub = index - currentIndex
            if (lineSub > line.length)
                lineSub = line.length
            return Vec2d(lineSub, yIndex)
        }

        return Vec2d(0, yIndex)
    }

    private fun getIndexOnLine(line: String, x: Int): Int {

        var length = 0
        var index = 0
        while (index < line.length) {
            val chr = line[index]

            if (chr.toString() == "§" && isFormat(line[index + 1])) { // is format code
                index += 2
                continue
            }

            val width = fontRenderer.getCharWidth(chr)

            if (x >= length && x < length + width) { // the cursor is on the character
                val center = length + width / 2
                if (x < center) {
                    return index
                } else {
                    return index + 1
                }
            }

            length += width
            index++
        }

        if (x >= length)
            return line.length
        else
            return 0
    }

    private fun isFormat(char: Char): Boolean {
        return isFormatColor(char) || isFormatSpecial(char)
    }

    /**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    private fun isFormatColor(colorChar: Char): Boolean {
        return colorChar.toInt() >= 48 && colorChar.toInt() <= 57 || colorChar.toInt() >= 97 && colorChar.toInt() <= 102 || colorChar.toInt() >= 65 && colorChar.toInt() <= 70
    }

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    private fun isFormatSpecial(formatChar: Char): Boolean {
        return formatChar.toInt() >= 107 && formatChar.toInt() <= 111 || formatChar.toInt() >= 75 && formatChar.toInt() <= 79 || formatChar.toInt() == 114 || formatChar.toInt() == 82
    }

    internal fun wrapFormattedStringToWidth(str: String, wrapWidth: Int, sep: String): String {
        val i = this.sizeStringToWidth(str, wrapWidth)

        if (str.length <= i) {
            return str
        } else {
            val s = str.substring(0, i + 1)
            val c0 = str[i]
            val flag = c0.toInt() == 32 || c0.toInt() == 10
            val s1 = FontRenderer.getFormatFromString(s) + str.substring(i + 1) // + if (flag) 1 else 0
            return s + sep + this.wrapFormattedStringToWidth(s1, wrapWidth, sep)
        }
    }

    internal fun sizeStringToWidth(str: String, wrapWidth: Int): Int {
        val i = str.length
        var j = 0
        var k = 0
        var l = -1

        var flag = false
        while (k < i) {
            val c0 = str[k]

            when (c0) {
                '\n' -> --k
                ' ' -> {
                    l = k
                    j += fontRenderer.getCharWidth(c0)

                    if (flag) {
                        ++j
                    }
                }
                '\u00a7' -> {
                    if (k < i - 1) {
                        ++k
                        val c1 = str[k]

                        if (c1.toInt() != 108 && c1.toInt() != 76) {
                            if (c1.toInt() == 114 || c1.toInt() == 82 || isFormatColor(c1)) {
                                flag = false
                            }
                        } else {
                            flag = true
                        }
                    }
                }
                else -> {
                    j += fontRenderer.getCharWidth(c0)
                    if (flag) {
                        ++j
                    }
                }

            }

            if (c0.toInt() == 10) {
                ++k
                l = k
                break
            }

            if (j > wrapWidth) {
                break
            }
            ++k
        }

        return if (k != i && l != -1 && l < k) l else k
    }

}
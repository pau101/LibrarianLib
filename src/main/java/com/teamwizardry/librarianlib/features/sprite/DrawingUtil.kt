package com.teamwizardry.librarianlib.features.sprite

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object DrawingUtil {
    var isDrawing = false

    /**
     * Start drawing multiple quads to be pushed to the GPU at once
     */
    fun startDrawingSession() {
        Tessellator.getInstance().buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        isDrawing = true
    }

    /**
     * Finish drawing multiple quads and push them to the GPU
     */
    fun endDrawingSession() {
        Tessellator.getInstance().draw()
        isDrawing = false
    }

    private fun draw(minX: Float, minY: Float, maxX: Float, maxY: Float,
                     minU: Float, minV: Float, maxU: Float, maxV: Float) {
        val vb = Tessellator.getInstance().buffer
        vb.pos(minX.toDouble(), maxY.toDouble(), 0.0).tex(minU.toDouble(), maxV.toDouble()).endVertex()
        vb.pos(maxX.toDouble(), maxY.toDouble(), 0.0).tex(maxU.toDouble(), maxV.toDouble()).endVertex()
        vb.pos(maxX.toDouble(), minY.toDouble(), 0.0).tex(maxU.toDouble(), minV.toDouble()).endVertex()
        vb.pos(minX.toDouble(), minY.toDouble(), 0.0).tex(minU.toDouble(), minV.toDouble()).endVertex()
    }

    /**
     * **!!! Use [Sprite.draw] or [Sprite.draw] instead !!!**

     *
     * Draw a sprite at a location with the width and height specified.
     * @param sprite The sprite to draw
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    fun draw(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Float, height: Float) {

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val minU = sprite.minU(animFrames)
        val minV = sprite.minV(animFrames)
        val maxU = sprite.maxU(animFrames)
        val maxV = sprite.maxV(animFrames)

        val sliceX = sprite.inWidth * sprite.nineSliceSizeW
        val sliceY = sprite.inHeight * sprite.nineSliceSizeH

        val sliceU = (maxU-minU) * sprite.nineSliceSizeW
        val sliceV = (maxV-minV) * sprite.nineSliceSizeH


        if(sprite.nineSliceSizeW != 0f || sprite.nineSliceSizeH != 0f) {
            // top left
            draw(
                    minX, minY, minX + sliceX, minY + sliceY,
                    minU, minV, minU + sliceU, minV + sliceV
            )
            // top right
            draw(
                    maxX - sliceX, minY, maxX, minY + sliceY,
                    maxU - sliceU, minV, maxU, minV + sliceV
            )
            // bottom left
            draw(
                    minX, maxY - sliceY, minX + sliceX, maxY,
                    minU, maxV - sliceV, minU + sliceU, maxV
            )
            // bottom right
            draw(
                    maxX - sliceX, maxY - sliceY, maxX, maxY,
                    maxU - sliceU, maxV - sliceV, maxU, maxV
            )


            // top
            draw(
                    minX + sliceX, minY, maxX - sliceX, minY + sliceY,
                    minU + sliceU, minV, maxU - sliceU, minV + sliceV
            )
            // bottom
            draw(
                    minX + sliceX, maxY - sliceY, maxX - sliceX, maxY,
                    minU + sliceU, maxV - sliceV, maxU - sliceU, maxV
            )
            // left
            draw(
                    minX, minY + sliceY, minX + sliceX, maxY - sliceY,
                    minU, minV + sliceV, minU + sliceU, maxV - sliceV
            )
            // right
            draw(
                    maxX - sliceX, minY + sliceY, maxX, maxY - sliceY,
                    maxU - sliceU, minV + sliceV, maxU, maxV - sliceV
            )

            // center
            draw(
                    minX + sliceX, minY + sliceY, maxX - sliceX, maxY - sliceY,
                    minU + sliceU, minV + sliceV, maxU - sliceU, maxV - sliceV
            )
        } else {
            draw(
                    minX, minY, maxX, maxY,
                    minU, minV, maxU, maxV
            )
        }

        if (!isDrawing)
            tessellator.draw()
    }

    /**
     * **!!! Use [Sprite.drawClipped] instead !!!**

     *
     * Draw a sprite at a location with the width and height specified by clipping or tiling instead of stretching/squishing
     * @param sprite The sprite to draw
     * @param x The x position to draw at
     * @param y The y position to draw at
     * @param width The width to draw the sprite
     * @param height The height to draw the sprite
     */
    fun drawClipped(sprite: ISprite, animFrames: Int, x: Float, y: Float, width: Int, height: Int, reverseX: Boolean, reverseY: Boolean) {
        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        if (!isDrawing)
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val minU = sprite.minU(animFrames)
        val minV = sprite.minV(animFrames)
        val maxU = sprite.maxU(animFrames)
        val maxV = sprite.maxV(animFrames)

        val texW = sprite.inWidth.toFloat()
        val texH = sprite.inHeight.toFloat()

        val sliceX = sprite.inWidth * sprite.nineSliceSizeW
        val sliceY = sprite.inHeight * sprite.nineSliceSizeH

        val sliceU = (maxU-minU) * sprite.nineSliceSizeW
        val sliceV = (maxV-minV) * sprite.nineSliceSizeH

        val sliceW = texW * sprite.nineSliceSizeW
        val sliceH = texH * sprite.nineSliceSizeH

        if(sprite.nineSliceSizeW != 0f || sprite.nineSliceSizeH != 0f) {
            // top left
            draw(
                    minX, minY, minX + sliceX, minY + sliceY,
                    minU, minV, minU + sliceU, minV + sliceV
            )
            // top right
            draw(
                    maxX - sliceX, minY, maxX, minY + sliceY,
                    maxU - sliceU, minV, maxU, minV + sliceV
            )
            // bottom left
            draw(
                    minX, maxY - sliceY, minX + sliceX, maxY,
                    minU, maxV - sliceV, minU + sliceU, maxV
            )
            // bottom right
            draw(
                    maxX - sliceX, maxY - sliceY, maxX, maxY,
                    maxU - sliceU, maxV - sliceV, maxU, maxV
            )


            // top
            drawSegments(
                    texW-sliceW*2, sliceH, reverseX, reverseY,

                    minX + sliceX, minY, maxX - sliceX, minY + sliceY,
                    minU + sliceU, minV, maxU - sliceU, minV + sliceV
            )
            // bottom
            drawSegments(
                    texW-sliceW*2, sliceH, reverseX, reverseY,

                    minX + sliceX, maxY - sliceY, maxX - sliceX, maxY,
                    minU + sliceU, maxV - sliceV, maxU - sliceU, maxV
            )
            // left
            drawSegments(
                    sliceW, texH-sliceH*2, reverseX, reverseY,

                    minX, minY + sliceY, minX + sliceX, maxY - sliceY,
                    minU, minV + sliceV, minU + sliceU, maxV - sliceV
            )
            // right
            drawSegments(
                    sliceW, texH-sliceH*2, reverseX, reverseY,

                    maxX - sliceX, minY + sliceY, maxX, maxY - sliceY,
                    maxU - sliceU, minV + sliceV, maxU, maxV - sliceV
            )

            // center
            drawSegments(
                    texW-sliceW*2, texH-sliceH*2, reverseX, reverseY,

                    minX + sliceX, minY + sliceY, maxX - sliceX, maxY - sliceY,
                    maxU + sliceU, minV + sliceV, maxU - sliceU, maxV - sliceV
            )
        } else {
            drawSegments(
                    texW, texH, reverseX, reverseY,

                    minX, minY, maxX, maxY,
                    minU, minV, maxU, maxV
            )
        }

        if (!isDrawing)
            tessellator.draw()
    }

    private fun drawSegments(texWidth: Float, texHeight: Float, reverseX: Boolean, reverseY: Boolean,
                             minX: Float, minY: Float, maxX: Float, maxY: Float,
                             minU: Float, minV: Float, maxU: Float, maxV: Float) {
        val xSegments = segments(minX, texWidth, maxX-minX, reverseX)
        val ySegments = segments(minY, texHeight, maxY-minY, reverseY)

        xSegments.forEach { xSeg ->
            val _minX = if(reverseX && xSeg.coord < minX) minX else xSeg.coord
            val _maxX = _minX + (maxX-minX)*xSeg.sizeFraction
            val _minU = if(reverseX && xSeg.coord < minX) maxU - (maxU-minU)*xSeg.sizeFraction else minU
            val _maxU = if(reverseX && xSeg.coord > minX) minU + (maxU-minU)*xSeg.sizeFraction else maxU

            ySegments.forEach { ySeg ->
                val _minY = if(reverseY && ySeg.coord < minY) minY else ySeg.coord
                val _maxY = _minY + (maxY-minY)*ySeg.sizeFraction
                val _minV = if(reverseY && ySeg.coord < minY) maxV - (maxV-minV)*xSeg.sizeFraction else minV
                val _maxV = if(reverseY && ySeg.coord > minY) minV + (maxV-minV)*xSeg.sizeFraction else maxV

                draw(
                        _minX, _minY, _maxX, _maxY,
                        _minU, _minV, _maxU, _maxV
                )
            }
        }
    }

    /**
     * Creates a number of segments of size [segmentSize] filling a space [finalSize] in length.
     *
     * When [reversed] is true, the partial segment will be located at the beginning of the list and will have a
     * [Segment.coord] appropriately smaller than [start].
     */
    private fun segments(start: Float, segmentSize: Float, finalSize: Float, reversed: Boolean): List<Segment> {
        val segments = mutableListOf<Segment>()

        // add all the full segments
        val fullSegmentCount = Math.floor(finalSize / segmentSize.toDouble()).toInt()
        (0 until fullSegmentCount).forEach { i ->
            segments.add(Segment(start + segmentSize * i, 1f))
        }

        val partialSegmentSize = finalSize - (segmentSize * fullSegmentCount)
        if(partialSegmentSize != 0f) {
            if(reversed) {
                segments.add(0, Segment(start - segmentSize * (1-partialSegmentSize/segmentSize), partialSegmentSize/segmentSize))
            } else {
                segments.add(Segment(start - segmentSize * fullSegmentCount, partialSegmentSize/segmentSize))
            }
        }

        return segments
    }

    private data class Segment(val coord: Float, val sizeFraction: Float)
}

package com.teamwizardry.librarianlib.client.font

import java.awt.Rectangle

/**
 * Created by TheCodeWarrior
 */
class Packer(size: Int, val maxSize: Int) {
    var padding = 0
    protected val rootNode = mutableListOf<Node>()

    var width = size
        private set
    var height = size
        private set

    init {
        val node = Node()
        node.rect.width = width
        node.rect.height = height
        rootNode.add(node)
    }

    fun pack(w: Int, h: Int): Rectangle? {
        val rect = Rectangle(0, 0, w+padding, h+padding)

        var node: Node? = null
        for(root in rootNode) {
            node = insert(root, rect)
            if(node != null)
                break
        }
        if(node == null) {
            if(expand())
                node = insert(rootNode.last(), rect)
        }
        if(node == null)
            return null

        node.full = true

        rect.x = node.rect.x
        rect.y = node.rect.y
        rect.width = node.rect.width-padding
        rect.height = node.rect.height-padding

        return rect
    }

    fun expand(): Boolean {
        val oldH = height
        val oldW = width

        val node = Node()
        // we're doubling the size either way, so the only thing that changes is where it's positioned
        node.rect.width = oldW
        node.rect.height = oldH

        if(height > width) {
            if(width * 2 > maxSize)
                return false
            width *= 2
            node.rect.x = oldW
        } else {
            if(height * 2 > maxSize)
                return false
            height *= 2
            node.rect.y = oldH
        }
        rootNode.add(node)

        return true
    }

    private fun insert(node: Node, rect: Rectangle): Node? {
        var left = node.leftChild
        var right = node.rightChild
        if (!node.full && left != null && right != null) { // node
            var newNode = insert(left, rect)
            if (newNode == null) newNode = insert(right, rect)
            return newNode
        } else {
            if (node.full)
                return null
            if (node.rect.width == rect.width && node.rect.height == rect.height)
                return node
            if (node.rect.width < rect.width || node.rect.height < rect.height)
                return null

            left = Node()
            right = Node()

            node.leftChild = left
            node.rightChild = right

            val deltaWidth = node.rect.width.toInt() - rect.width.toInt()
            val deltaHeight = node.rect.height.toInt() - rect.height.toInt()
            if (deltaWidth > deltaHeight) {
                left.rect.x = node.rect.x
                left.rect.y = node.rect.y
                left.rect.width = rect.width
                left.rect.height = node.rect.height

                right.rect.x = node.rect.x + rect.width
                right.rect.y = node.rect.y
                right.rect.width = node.rect.width - rect.width
                right.rect.height = node.rect.height
            } else {
                left.rect.x = node.rect.x
                left.rect.y = node.rect.y
                left.rect.width = node.rect.width
                left.rect.height = rect.height

                right.rect.x = node.rect.x
                right.rect.y = node.rect.y + rect.height
                right.rect.width = node.rect.width
                right.rect.height = node.rect.height - rect.height
            }

            return insert(left, rect)
        }
    }

    protected class Node {
        var leftChild: Node? = null
        var rightChild: Node? = null
        val rect = Rectangle()
        var full: Boolean = false
    }
}






/*
class GuillotineStrategy : PackStrategy {
    override fun pack(w: Int, h: Int): Rectangle {
        val padding = packer.padding
        rect.width += padding.toFloat()
        rect.height += padding.toFloat()
        var node = insert(page.root, rect)
        if (node == null) {
            // Didn't fit, pack into a new page.
            page = GuillotinePage(packer)
            packer.pages.add(page)
            node = insert(page.root, rect)
        }
        node!!.full = true
        rect.set(node.rect.x, node.rect.y, node.rect.width - padding, node.rect.height - padding)
        return page
    }

    private fun insert(node: Node, rect: Rectangle): Node? {
        if (!node.full && node.leftChild != null && node.rightChild != null) {
            var newNode = insert(node.leftChild, rect)
            if (newNode == null) newNode = insert(node.rightChild, rect)
            return newNode
        } else {
            if (node.full) return null
            if (node.rect.width == rect.width && node.rect.height == rect.height) return node
            if (node.rect.width < rect.width || node.rect.height < rect.height) return null

            node.leftChild = Node()
            node.rightChild = Node()

            val deltaWidth = node.rect.width.toInt() - rect.width.toInt()
            val deltaHeight = node.rect.height.toInt() - rect.height.toInt()
            if (deltaWidth > deltaHeight) {
                node.leftChild!!.rect.x = node.rect.x
                node.leftChild!!.rect.y = node.rect.y
                node.leftChild!!.rect.width = rect.width
                node.leftChild!!.rect.height = node.rect.height

                node.rightChild!!.rect.x = node.rect.x + rect.width
                node.rightChild!!.rect.y = node.rect.y
                node.rightChild!!.rect.width = node.rect.width - rect.width
                node.rightChild!!.rect.height = node.rect.height
            } else {
                node.leftChild!!.rect.x = node.rect.x
                node.leftChild!!.rect.y = node.rect.y
                node.leftChild!!.rect.width = node.rect.width
                node.leftChild!!.rect.height = rect.height

                node.rightChild!!.rect.x = node.rect.x
                node.rightChild!!.rect.y = node.rect.y + rect.height
                node.rightChild!!.rect.width = node.rect.width
                node.rightChild!!.rect.height = node.rect.height - rect.height
            }

            return insert(node.leftChild, rect)
        }
    }

    internal class Node {
        var leftChild: Node? = null
        var rightChild: Node? = null
        val rect = Rectangle()
        var full: Boolean = false
    }

    internal class GuillotinePage(packer: PixmapPacker) : Page(packer) {
        var root: Node

        init {
            root = Node()
            root.rect.x = packer.padding.toFloat()
            root.rect.y = packer.padding.toFloat()
            root.rect.width = (packer.pageWidth - packer.padding * 2).toFloat()
            root.rect.height = (packer.pageHeight - packer.padding * 2).toFloat()
        }
    }
}

interface PackStrategy {
    /** Returns the page the rectangle should be placed in and modifies the specified rectangle position.  */
    fun pack(width: Int, height: Int): Rectangle
}*/
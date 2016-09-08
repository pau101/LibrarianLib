package com.teamwizardry.librarianlib.client.newbook.backend

import com.teamwizardry.librarianlib.common.util.math.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class Book(val loc: ResourceLocation) {

    val bookSprites = mutableListOf<BookSprite>()
    var textureMap = ResourceLocation(loc.resourceDomain, "docs/"+loc.resourcePath+"/textureMap.png")

}

class BookSprite(var spriteName: String, var location: Vec2d, var size: Vec2d, var zIndex: Int)
package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.dsl
import com.teamwizardry.librarianlib.features.gui.pastry.PastryBackground
import com.teamwizardry.librarianlib.features.gui.pastry.PastryButton

class GuiTestDSL : GuiBase(100, 100) {
    init {
        mainComponents.dsl { main ->
            +PastryBackground() % { bg ->
                -{ bg.layout.boundsEqualTo(main) }

                val button1 = +PastryButton("Button 1") % {
                    !{ _: GuiComponentEvents.MouseClickEvent ->
                        println("Clicked 1!")
                    }
                    -{
                        this.layout.left eq main.layout.left
                        this.layout.top eq main.layout.top
                    }
                }
                val button2 = +PastryButton("Button 2") % {
                    !{ _: GuiComponentEvents.MouseClickEvent ->
                        println("Clicked 2!")
                    }
                    -{
                        this.layout.left eq main.layout.left
                        this.layout.top eq button1.layout.bottom + 2
                    }
                }
                val button3 = +PastryButton("Button 3") % {
                    !{ _: GuiComponentEvents.MouseClickEvent ->
                        println("Clicked 3!")
                    }
                    -{
                        this.layout.left eq main.layout.left
                        this.layout.top eq button2.layout.bottom + 2
                    }
                }
            }
        }

    }
}

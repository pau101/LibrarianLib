package com.teamwizardry.librarianlib.gui.template;

import com.teamwizardry.librarianlib.gui.components.ComponentSliderTray;
import com.teamwizardry.librarianlib.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.gui.components.ComponentText;
import com.teamwizardry.librarianlib.book.gui.GuiBook;
import net.minecraft.item.ItemStack;

public class SliderTemplate {

	public static ComponentSliderTray text(int posY, String text) {
		ComponentSliderTray slider = new ComponentSliderTray(0, posY, -120, 0);
		ComponentText textComp = new ComponentText(6, 4).setup((comp)-> {
			comp.text.setValue(text);
			comp.wrap.setValue(113);
			comp.unicode.setValue(true);
		});
		
		slider.add(new ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0, 133, 8 + textComp.getLogicalSize().heightI()));
		slider.add(textComp);
		
		return slider;
	}
	
	public static ComponentSliderTray recipe(int posY, ItemStack[][] recipe) {
		ComponentSliderTray slider = new ComponentSliderTray(0, posY, -120, 0);
		slider.add(new ComponentSprite(GuiBook.SLIDER_RECIPE, 0, 0));
		
		return slider;
	}
	
}
package com.teamwizardry.librarianlib.client.gui;

import com.teamwizardry.librarianlib.common.core.LibLibObfuscation;
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper;
import net.minecraft.client.gui.FontRenderer;
import sun.tools.javac.SourceClass;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;

import static com.google.common.base.Throwables.propagate;

/**
 * Created by TheCodeWarrior
 */
public class WrappedStringManagerMethodHandles {
	private static final MethodHandle getter_FontRenderer_glyphWidth = MethodHandleHelper.handleForField(FontRenderer.class, true, LibLibObfuscation.FontRenderer_glyphWidth);
	
	public static byte[] get_FontRenderer_glyphWidth(@Nonnull FontRenderer instance) {
		try {
			return (byte[]) getter_FontRenderer_glyphWidth.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
}

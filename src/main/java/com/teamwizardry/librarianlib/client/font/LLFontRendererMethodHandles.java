package com.teamwizardry.librarianlib.client.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper;
import sun.tools.javac.SourceClass;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;

import static com.google.common.base.Throwables.propagate;

/**
 * Created by TheCodeWarrior
 */
public class LLFontRendererMethodHandles {
	
	private static final MethodHandle getter_BitmapFontCache_pageVertices = MethodHandleHelper.handleForField(BitmapFontCache.class, true, "pageVertices");
	private static final MethodHandle getter_BitmapFontCache_idx= MethodHandleHelper.handleForField(BitmapFontCache.class, true, "idx");
	
	public static float[][] get_BitmapFontCache_pageVertices(@Nonnull BitmapFontCache instance) {
		try {
			return (float[][]) getter_BitmapFontCache_pageVertices.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
	
	public static int[] get_BitmapFontCache_idx(@Nonnull BitmapFontCache instance) {
		try {
			return (int[]) getter_BitmapFontCache_idx.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
}

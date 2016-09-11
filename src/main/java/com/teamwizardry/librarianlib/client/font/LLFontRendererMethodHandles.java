package com.teamwizardry.librarianlib.client.font;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
	private static final MethodHandle getter_FreeTypeBitmapFontData_packer = MethodHandleHelper.handleForField(FreeTypeFontGenerator.FreeTypeBitmapFontData.class, true, "packer");
	private static final MethodHandle setter_FreeTypeBitmapFontData_packer = MethodHandleHelper.handleForField(FreeTypeFontGenerator.FreeTypeBitmapFontData.class, false, "packer");
	private static final MethodHandle getter_PixmapPacker_packStrategy = MethodHandleHelper.handleForField(PixmapPacker.class, true, "packStrategy");
	
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
	
	public static PixmapPacker get_FreeTypeBitmapFontData_packer(@Nonnull FreeTypeFontGenerator.FreeTypeBitmapFontData instance) {
		try {
			return (PixmapPacker) getter_FreeTypeBitmapFontData_packer.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
	public static void set_FreeTypeBitmapFontData_packer(@Nonnull FreeTypeFontGenerator.FreeTypeBitmapFontData instance, PixmapPacker packer) {
		try {
			setter_FreeTypeBitmapFontData_packer.invokeExact(instance, packer);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
	public static PixmapPacker.PackStrategy get_PixmapPacker_packStrategy(@Nonnull PixmapPacker instance) {
		try {
			return (PixmapPacker.PackStrategy) getter_PixmapPacker_packStrategy.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
}

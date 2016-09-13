package com.teamwizardry.librarianlib.client.font;

import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import com.teamwizardry.librarianlib.common.util.MethodHandleHelper;
import sun.font.Font2D;
import sun.font.FontStrike;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.invoke.MethodHandle;

import static com.google.common.base.Throwables.propagate;

/**
 * Created by TheCodeWarrior
 */
public class FontMethodHandles {
	private static final MethodHandle method_Font_getFont2D = MethodHandleHelper.handleForMethod(Font.class, new String[] {"getFont2D"});
	private static final MethodHandle method_FontStrike_getGlyphOutlineBounds = MethodHandleHelper.handleForMethod(FontStrike.class, new String[] {"getGlyphOutlineBounds"}, int.class);
	private static final MethodHandle method_FontStrike_getGlyphAdvance = MethodHandleHelper.handleForMethod(FontStrike.class, new String[] {"getGlyphAdvance"}, int.class);
	
	public static Font2D call_Font_getFont2D(@Nonnull Font instance) {
		try {
			return (Font2D) method_Font_getFont2D.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
	
	public static Rectangle2D.Float call_FontStrike_getGlyphOutlineBounds(@Nonnull FontStrike instance, int codepoint) {
		try {
			return (Rectangle2D.Float) method_FontStrike_getGlyphOutlineBounds.invokeExact(instance, codepoint);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
	
	public static float call_FontStrike_getGlyphAdvance(@Nonnull FontStrike instance, int codepoint) {
		try {
			return (float) method_FontStrike_getGlyphAdvance.invokeExact(instance, codepoint);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
}

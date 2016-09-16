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
	
	public static Font2D call_Font_getFont2D(@Nonnull Font instance) {
		try {
			return (Font2D) method_Font_getFont2D.invokeExact(instance);
		} catch (Throwable t) {
			throw propagate(t);
		}
	}
}

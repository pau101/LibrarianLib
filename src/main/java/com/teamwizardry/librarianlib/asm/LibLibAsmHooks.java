package com.teamwizardry.librarianlib.asm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.core.client.GlowingHandler;
import com.teamwizardry.librarianlib.core.client.RenderHookHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

/**
 * @author WireSegal
 *         Created at 11:34 AM on 4/29/17.
 */
@SuppressWarnings("unused")
public class LibLibAsmHooks {
    public static final LibLibAsmHooks INSTANCE = new LibLibAsmHooks();
    // Elad's experiments
    // Run at your own risk
    // i'm serious, i wrote this code with wire when he was here in june.
    // this, while not a significant slowdown, will HORRIFY developers
    // it's not that bad and if i were by my own, i'd use it
    // but DON'T USE IT PEOPLE NEED TO LIKE YOU
    // don't remove, i'm sentimental af
    // see: asm transformer
    public static Multimap<String, Consumer<Object>> methods = HashMultimap.create();
    private static float x, y;

    public static void methodHook(Object instance, String name) {
        if (methods.containsKey(name))
            methods.get(name).forEach(it -> it.accept(instance));
    }

    public static void hook(String name, Consumer<Object> callback) {
        methods.put(name, callback);
    }

    // End Elad's stuff

    @SideOnly(Side.CLIENT)
    public static void renderHook(ItemStack stack, IBakedModel model) {
        RenderHookHandler.runItemHook(stack, model);
    }

    @SideOnly(Side.CLIENT)
    public static void renderHook(BlockModelRenderer blockModelRenderer, IBlockAccess world, IBakedModel model, IBlockState state, BlockPos pos, BufferBuilder vertexBuffer) {
        RenderHookHandler.runBlockHook(blockModelRenderer, world, model, state, pos, vertexBuffer);
    }

    @SideOnly(Side.CLIENT)
    public static void renderHook(BlockModelRenderer blockModelRenderer, IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder vertexBuffer) {
        RenderHookHandler.runFluidHook(blockModelRenderer, world, state, pos, vertexBuffer);
    }

    @SideOnly(Side.CLIENT)
    public void maximizeGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow()) {
            x = OpenGlHelper.lastBrightnessX;
            y = OpenGlHelper.lastBrightnessY;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }
    }

    @SideOnly(Side.CLIENT)
    public void returnGlowLightmap() {
        if (GlowingHandler.getEnchantmentGlow())
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, x, y);
    }

    @SideOnly(Side.CLIENT)
    public boolean usePotionGlow() {
        return GlowingHandler.getPotionGlow();
    }
}

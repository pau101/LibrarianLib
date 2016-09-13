package com.teamwizardry.librarianlib.common.util

import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.publicLookup

/**
 * @author WireSegal
 * Created at 6:49 PM on 8/14/16.
 */
object MethodHandleHelper {

    /**
     * Reflects a method from a class, and provides a MethodHandle for it.
     * Methodhandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T> handleForMethod(clazz: Class<T>, methodNames: Array<String>, vararg methodClasses: Class<*>): MethodHandle {
        val m = ReflectionHelper.findMethod<T>(clazz, null, methodNames, *methodClasses)
        return publicLookup().unreflect(m)
    }

    /**
     * Reflects a field from a class, and provides a MethodHandle for it.
     * MethodHandles MUST be invoked from java code, due to the way [@PolymorphicSignature] works.
     */
    @JvmStatic
    fun <T> handleForField(clazz: Class<T>, getter: Boolean, vararg fieldNames: String): MethodHandle {
        val f = ReflectionHelper.findField(clazz, *fieldNames)
        return if (getter) publicLookup().unreflectGetter(f) else publicLookup().unreflectSetter(f)
    }

    /* === Copy-paste stuff for MethodHandles. ===
    ========== !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ==========
    !!!!!!!!!! Make sure the method is static, and that the methodhandle field is a static final field !!!!!!!!!!
    ========== !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ==========
                                  $%&$%&$%&$%& ^^^^ READ THAT ^^^^ $%&$%&$%&$%&

    Getter/Setter: (change the true/false argument to true for get, false for set)
                   (change getter and get for setter and set if you're doing a setter)

        private static final MethodHandle getter_ClassName_memberName = MethodHandleHelper.handleForField(ClassName.class, true, YourLibObfuscation.ClassName_MemberName|new String[] {"memberName"});

        public static MemberType get_ClassName_memberName(@Nonnull ClassName instance) {
            try {
                return (MemberType) getter_ClassName_MemberName.invokeExact(instance);
            } catch (Throwable t) {
                throw propagate(t);
            }
        }

    Method call: (make method return void if the method is a void method)
                 (remember that for primitives you have to do something like int.class instead of Integer.class)

        private static final MethodHandle method_ClassName_methodName = MethodHandleHelper.handleForMethod(ClassName.class, YourLibObfuscation.ClassName_methodName|new String[] {"methodName"}, paramClasses...);

        public static Return_Type call_ClassName_methodName(@Nonnull ClassName instance, ParamType param) {
            try {
                return (Return_Type) method_ClassName_methodName.invokeExact(instance, param);
            } catch (Throwable t) {
                throw propagate(t);
            }
        }

     */
}

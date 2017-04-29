package com.teamwizardry.librarianlib.core;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.Objects;

public class AsmTransformer implements IClassTransformer, Opcodes {
    String whateverFullyQualifiedNameClass; //deobf
    String whateverMethodName; //test for deobf name || obf name

    @Override
    public byte[] transform(String name, String deobfName, byte[] basicClass) {
        if (Objects.equals(deobfName, whateverFullyQualifiedNameClass)) {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);

            for (MethodNode methodNode : node.methods) {
                if (Objects.equals(whateverMethodName, methodNode.name)) {
                    for (AbstractInsnNode nodeInMethod : methodNode.instructions.toArray()) {
                        if (nodeInMethod.getOpcode() == INVOKESPECIAL) { //or whatever
                            methodNode.instructions.insert(nodeInMethod, new MethodInsnNode(INVOKESTATIC, "whatever", "whatever", "whatever"));
                        }
                    }
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }
        return basicClass;

    }
}

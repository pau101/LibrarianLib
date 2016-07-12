package com.teamwizardry.librarianlib.cloth;

import net.minecraft.util.math.Vec3d;

public class Link {
	public PointMass a, b;
	public float distance;
	public float strength;
	
	public Link(PointMass a, PointMass b, float strength) {
		this.a = a;
		this.b = b;
		this.strength = strength;
		this.distance = (float) a.pos.subtract(b.pos).lengthVector();
	}
	
	public Link(PointMass a, PointMass b, float distance, float strength) {
		this.a = a;
		this.b = b;
		this.strength = strength;
		this.distance = distance;
	}
	
	public void resolve() {
		if(a.pin && b.pin)
			return;
		
		Vec3d posDiff = a.pos.subtract(b.pos);
		double d = posDiff.lengthVector();
		
		double difference = (distance - d)/d;
		
		if(!a.pin && !b.pin) // neither are pinned
			difference = difference / 2.0;
		
		Vec3d translate = posDiff.scale(difference);
		
		if(!a.pin) {
			a.pos = a.pos.add(translate);
		}
		
		if(!b.pin) {
			b.pos = b.pos.subtract(translate);
		}
	}
}

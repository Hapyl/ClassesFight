package ru.hapyl.classesfight.utils.pn;

import ru.hapyl.classesfight.classes.ClassManager;

public class ClassUpdatePatch extends Patch {

	private final ClassManager clazz;

	public ClassUpdatePatch(ClassManager clazz) {
		super(Type.CLASS_UPDATE);
		this.clazz = clazz;
	}

	public ClassManager getClazz() {
		return clazz;
	}

}

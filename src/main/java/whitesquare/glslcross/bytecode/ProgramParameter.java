package whitesquare.glslcross.bytecode;

import java.util.Objects;

public class ProgramParameter {
	public enum Type {IN, OUT};
	public Type type;
	public int slot;
	public int size;
	public String name;
	
	ProgramParameter(Type type, int slot, int size, String name) {
		this.type = type;
		this.slot = slot;
		this.size = size;
		this.name = name;
	}

	@Override
	protected ProgramParameter clone() {
		return new ProgramParameter(type, slot, size, name);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProgramParameter that = (ProgramParameter) o;
		return slot == that.slot &&
				size == that.size &&
				type == that.type &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, slot, size, name);
	}
}

package whitesquare.glslcross.bytecode;

import java.util.Objects;

public class Instruction implements Cloneable {
	public enum Type {NONE, INTEGER, FLOAT, STRING};
	
	public Bytecode bytecode;
	public Type type;
	
	public int valueInt;
	public float valueFloat;
	public String valueString;
	
	public int stackIn; // Only used to verify functions
	public int stackOut;
	
	public Instruction(Bytecode bytecode) {
		this.bytecode = bytecode;
		this.type = Type.NONE;
	}
	
	public Instruction(Bytecode bytecode, int value) {
		this.bytecode = bytecode;
		this.valueInt = value;
		this.type = Type.INTEGER;
	}
	
	public Instruction(Bytecode bytecode, float value) {
		this.bytecode = bytecode;
		this.valueFloat = value;
		this.type = Type.FLOAT;
	}
	
	public Instruction(Bytecode bytecode, String value) {
		this.bytecode = bytecode;
		this.valueString = value;
		this.type = Type.STRING;
	}
	
	public String toString() {
		String str = bytecode.name();
		switch (type) {
			case INTEGER: str += " " + valueInt; break;
			case FLOAT: str += " " + valueFloat; break;
			case STRING: str += " " + valueString; break;
			default: break;
		}
		return str;
	}

	@Override
	protected Instruction clone() {
		try {
			return (Instruction) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Instruction that = (Instruction) o;
		return valueInt == that.valueInt &&
				Float.compare(that.valueFloat, valueFloat) == 0 &&
				stackIn == that.stackIn &&
				stackOut == that.stackOut &&
				bytecode == that.bytecode &&
				type == that.type &&
				Objects.equals(valueString, that.valueString);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bytecode, type, valueInt, valueFloat, valueString, stackIn, stackOut);
	}
}

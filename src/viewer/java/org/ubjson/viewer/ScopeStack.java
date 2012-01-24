package org.ubjson.viewer;

public class ScopeStack {
	private int pos;
	private Scope[] levels;

	public ScopeStack() {
		// Support a scope depth up to 128-levels.
		levels = new Scope[128];
	}

	public void reset() {
		pos = -1;
	}

	public void push(Scope scope) {
		levels[++pos] = scope;
	}

	public Scope peek() {
		return (pos < 0 ? null : levels[pos]);
	}

	public Scope pop() {
		return levels[pos--];
	}
}
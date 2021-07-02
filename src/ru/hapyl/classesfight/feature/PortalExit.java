package ru.hapyl.classesfight.feature;

public class PortalExit {

	private final BlockLocation ref;
	private final BlockLocation exit;

	public PortalExit(BlockLocation ref, BlockLocation exit) {
		this.ref = ref;
		this.exit = exit;
	}

	public BlockLocation getRef() {
		return ref;
	}

	public BlockLocation getExit() {
		return exit;
	}
}

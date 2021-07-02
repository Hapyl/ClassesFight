package ru.hapyl.classesfight.utils.candle;

import org.bukkit.entity.Player;

public class CandleData {

	private final Player player;
	private Candles candle;
	private boolean randomOffset;

	public CandleData(Player player, Candles candle, boolean randomOffset) {
		this.player = player;
		this.candle = candle;
		this.randomOffset = randomOffset;
	}

	public void setCandle(Candles candle) {
		this.candle = candle;
	}

	public void setRandomOffset(boolean randomOffset) {
		this.randomOffset = randomOffset;
	}

	public Player getPlayer() {
		return player;
	}

	public Candles getCandle() {
		return candle;
	}

	public boolean isRandomOffset() {
		return randomOffset;
	}
}

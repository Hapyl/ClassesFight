package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.Validator;

import java.util.List;

public class ParticleCommand extends SimplePlayerCommand {

	public ParticleCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(Player player, String[] args) {
		// particle PARTICLE int ox oy oz double
		if (args.length == 6) {
			Particle particle = Validator.getEnumValue(Particle.class, args[0]);
			int amount = Validator.getInt(args[1]);
			double offsetX = Validator.getDouble(args[2]);
			double offsetY = Validator.getDouble(args[3]);
			double offsetZ = Validator.getDouble(args[4]);
			float speed = (float)Validate.getFloat(args[5]);

			if (particle == null || amount == -999 || offsetX == -999D || offsetY == -999D || offsetZ == -999D || speed == -999D) {
				player.sendMessage("&cInvalid arguments provided.");
				return;
			}

			PlayerLib.spawnParticle(player.getLocation().add(0.0d, 2.0d, 0.0d), particle, amount, offsetX, offsetY, offsetZ, speed);
			Chat.sendMessage(player, "&aSpawned x%s %s (%s, %s, %s) with speed %s above your head!", amount, particle.name(), offsetX, offsetY, offsetZ, speed);

		}
		else {
			Chat.sendMessage(player, "&cInvalid Usage! /particle (Particle) (Amount) (offsetX) (offsetY) (offsetZ) (speed)");
		}
	}

	@Override
	protected List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			final List<String> strings = Chat.tabCompleterSort(Chat.arrayToList(Particle.values()), args);
			String last = args[args.length - 1];
			for (Particle value : Particle.values()) {
				String k = value.name().toLowerCase();
				if (k.startsWith(last))
					strings.add(k);
			}
			return strings;
		}
		return null;
	}
}
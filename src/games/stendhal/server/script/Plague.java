package games.stendhal.server.script;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * @author hendrik
 */
public class Plague extends ScriptImpl {

	private static final int MAX_RING_COUNT = 2;

	@Override
	public void execute(final Player admin, final List<String> args) {

		// help
		if (args.size() == 0) {
			admin.sendPrivateText("/script Plague.class [ringcount] <creature>");
			return;
		}

		// extract position of admin

		final int x = admin.getX();
		final int y = admin.getY();
		sandbox.setZone(admin.getZone());

		int ringcount = MathHelper.parseIntDefault(args.get(0), -1);
		int startArgIndex = 1;
		if (ringcount == -1) {
			ringcount = 1;
			startArgIndex = 0;
		}
		
		// concatenate torn words into one
		
		String creatureClass = "";
		final List <String>  templist = args.subList(startArgIndex, args.size());
		for (final String part : templist) {
			creatureClass = creatureClass + part + " "; 
		}
		
		creatureClass  = creatureClass.trim();

		final Creature tempCreature = sandbox.getCreature(creatureClass);
		
		if (tempCreature == null) {
			admin.sendPrivateText("No such creature");
		} else if (tempCreature.isRare() && System.getProperty("stendhal.testserver") == null) {
			// Rare creatures should not be summoned even in raids
			// Require parameter -Dstendhal.testserver=junk
			admin.sendPrivateText("Creatures with the rare property may only be summoned on test servers " 
												+ "which are activated with the vm parameter: -Dstendhal.testserver=junk");
		} else {
			final Creature creature = new RaidCreature(tempCreature);

			final int k = MathHelper.parseIntDefault(args.get(0), 1);
			if (k <= MAX_RING_COUNT) {
				for (int dx = -k; dx <= k; dx++) {
					for (int dy = -k; dy <= k; dy++) {
						if ((dx != 0) || (dy != 0)) {
							sandbox.add(creature, x + dx, y + dy + 1);
						}
					}
				}
			} else {
				admin.sendPrivateText("That's too many! Please keep <ringcount> less or equal to "
						+ MAX_RING_COUNT + ".");
			}
		}
	}
}

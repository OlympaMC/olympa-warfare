package fr.olympa.warfare.teamdeathmatch;

import java.lang.reflect.Constructor;

import fr.olympa.api.common.command.complex.Cmd;
import fr.olympa.api.common.command.complex.CommandContext;
import fr.olympa.api.spigot.command.ComplexCommand;
import fr.olympa.warfare.WarfarePermissions;
import fr.olympa.warfare.teamdeathmatch.gamestates.ChooseClassGameState;
import fr.olympa.warfare.teamdeathmatch.gamestates.EndGameState;
import fr.olympa.warfare.teamdeathmatch.gamestates.PlayingGameState;
import fr.olympa.warfare.teamdeathmatch.gamestates.WaitPlayingGameState;
import fr.olympa.warfare.teamdeathmatch.gamestates.WaitingGameState;

public class TDMCommand extends ComplexCommand {
	
	private TDM tdm;
	
	public TDMCommand(TDM tdm) {
		super(tdm.getPlugin(), "tdm", "Gère la partie de TDM.", WarfarePermissions.TDM_COMMAND_MANAGE);
		
		this.tdm = tdm;
		
		addArgumentParser("TEAM", Team.class);
	}
	
	@Cmd (args = "TEAM")
	public void finish(CommandContext cmd) {
		tdm.setState(x -> new EndGameState(x, cmd.getArgument(0, null)));
	}
	
	@Cmd (min = 1, syntax = "<state>", args = "waiting|class|waitplaying|playing")
	public void setState(CommandContext cmd) {
		String type = cmd.getArgument(0);
		
		Class<? extends GameState> clazz = switch (type) {
		
		case "waiting" -> WaitingGameState.class;
		case "class" -> ChooseClassGameState.class;
		case "waitplaying" -> WaitPlayingGameState.class;
		case "playing" -> PlayingGameState.class;
		default -> null;
		
		};
		
		if (clazz == null) {
			sendError("Game state inconnu.");
			return;
		}
		
		try {
			Constructor<? extends GameState> constructor = clazz.getDeclaredConstructor(TDM.class);
			tdm.setState(x -> {
				try {
					return constructor.newInstance(x);
				}catch (ReflectiveOperationException ex) {
					ex.printStackTrace();
				}
				return null;
			});
		}catch (ReflectiveOperationException ex) {
			sendError(ex);
			ex.printStackTrace();
		}
	}
	
}

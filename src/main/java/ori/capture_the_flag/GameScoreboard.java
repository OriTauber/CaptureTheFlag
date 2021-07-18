package ori.capture_the_flag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class GameScoreboard {
    private Main main;
    private Game_Manager game_manager;

    public GameScoreboard(Main main,Game_Manager game_manager) {

        this.game_manager = game_manager;
        this.main = main;
    }

    public void createScoreboard(Player p){
        if(!main.config.getBoolean("allow scoreboard"))
            return;
        String displayName = ChatColor.AQUA + "Capture The Flag" + ChatColor.RED + " !";

        ScoreboardManager sbm = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = sbm.getNewScoreboard();

        Objective obj = scoreboard.registerNewObjective("scoreboard","dummy",displayName);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score1 = obj.getScore(ChatColor.BLUE + "Blue " + ChatColor.GRAY + "team score: " + ChatColor.GOLD + game_manager.getBlue_Team().getTeamWins());
        score1.setScore(1);

        Score score2 = obj.getScore(ChatColor.RED + "Red " + ChatColor.GRAY + "team score: "  + ChatColor.GOLD + game_manager.getRed_Team().getTeamWins());
        score2.setScore(2);





        p.setScoreboard(scoreboard);

    }


}

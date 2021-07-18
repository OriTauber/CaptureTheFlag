package ori.capture_the_flag;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ori.capture_the_flag.commands.Build_Manager;
import ori.capture_the_flag.commands.TeamCommand;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin implements Listener {
    public boolean canBuild;
    public boolean gameStart;

    public HashMap<UUID, Boolean> getCanJoinTeamMap() {
        return canJoinTeamMap;
    }

    public HashMap<UUID,Boolean> canJoinTeamMap = new HashMap<>();



    private TeamCommand teamCmd;
    private Build_Manager build;
    private Game_Manager game;
    private GameScoreboard scoreboard;
    private Team team;
    public static GamePhase gamePhase;

    FileConfiguration config = getConfig();







    @Override
    public void onEnable() {

        canBuild = true;
        gameStart = false;

        build = new Build_Manager();
        game = new Game_Manager(build,this);
        teamCmd = new TeamCommand(game,build,this);
        team = new Team(build);
        scoreboard = new GameScoreboard(this,game);
        gamePhase = GamePhase.BEFORE_DRAW;


        getServer().getConsoleSender().sendMessage(ChatColor.UNDERLINE.toString() + ChatColor.GREEN + "Plugin is up!");
        getServer().getPluginManager().registerEvents(new ori.capture_the_flag.Game_Listener(build), this);
        getServer().getPluginManager().registerEvents(teamCmd,this);
        getServer().getPluginManager().registerEvents(game,this);
        getServer().getPluginManager().registerEvents(this,this);



        setConfigValues();


        for(Player p : Bukkit.getOnlinePlayers()){
            canJoinTeamMap.put(p.getUniqueId(),true);
            scoreboard.createScoreboard(p);
        }





        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setConfigValues(){
        config.addDefault("width",100);
        config.addDefault("height",100);
        config.addDefault("tall",6);
        config.addDefault("freeze time",3);
        config.addDefault("defended mode after freeze",2);
        config.addDefault("starting time",10);
        config.addDefault("allow scoreboard",true);
        config.addDefault("points-to-win",5);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (true) {
            if ("draw".equalsIgnoreCase(commandLabel) && sender instanceof Player) {
                if(canBuild) {
                    Player p = (Player) sender;
                    build.draw_Walls(p.getLocation(), config.getInt("width"),config.getInt("height"), config.getInt("tall"), p);
                    canBuild = false;

                    return true;
                }
                sender.sendMessage(ChatColor.DARK_RED + "The field has already been drawn");
                return true;
            }

            if ("select".equalsIgnoreCase(commandLabel) && sender instanceof Player) {
                Player p = (Player) sender;
                if(gamePhase == GamePhase.AFTER_DRAW) {


                    if (canJoinTeamMap.get(p.getUniqueId())) {
                        teamCmd.teamCommand(p);


                    } else {
                        p.sendMessage(ChatColor.DARK_RED + "You already joined a team, " + p.getName() + "!");
                    }
                }
                else{
                    p.sendMessage(ChatColor.DARK_RED + "You can select a team only before the game starts");

                }




            }
            if("start".equalsIgnoreCase(commandLabel) && sender instanceof Player){
                if(!sender.isOp()){
                    sender.sendMessage(ChatColor.DARK_RED + "You have to be an admin to start the game!");
                    return true;
                }
                Player p = (Player) sender;
                if(gamePhase == GamePhase.AFTER_DRAW) {

                    p.sendMessage(ChatColor.AQUA + "Starting the game...");
                    for(Player online : Bukkit.getOnlinePlayers()){
                        new BukkitRunnable(){
                            int seconds = config.getInt("starting time");

                            @Override
                            public void run() {
                                online.sendTitle(ChatColor.AQUA + "The game starts in " +ChatColor.BLUE +  seconds + ChatColor.AQUA + " seconds!",ChatColor.DARK_RED + "Last chances to join!",5,20,5);
                                seconds--;
                                if(seconds < 0){
                                    cancel();
                                    online.sendTitle(ChatColor.GOLD + "The game started!!!","",1,30,10);
                                    online.getWorld().playSound(online.getLocation(), Sound.BLOCK_ANVIL_HIT,5,5);
                                    gamePhase = GamePhase.GAME_START;
                                    return;

                                }


                            }

                        }.runTaskTimer(this,0,20L);
                    }



                }
                else{
                    p.sendMessage(ChatColor.DARK_RED + "Please draw the field first!");
                }

            }
        }
        return true;


    }
    @EventHandler
    public void join(PlayerJoinEvent e){
        canJoinTeamMap.put(e.getPlayer().getUniqueId(),true);
        scoreboard.createScoreboard(e.getPlayer());

    }


}
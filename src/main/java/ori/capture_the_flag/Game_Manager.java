package ori.capture_the_flag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ori.capture_the_flag.commands.Build_Manager;

import java.awt.*;
import java.util.HashMap;


public class Game_Manager implements Listener {
    private Team Red_Team;
    private Team Blue_Team;

    public Team getRed_Team() {
        return Red_Team;
    }

    public Team getBlue_Team() {
        return Blue_Team;
    }


    private Main main;
    private GameScoreboard scoreboard;



    private HashMap<String, Long> frozenPlayers = new HashMap<>();
    private HashMap<String, Boolean> hitablePlayers = new HashMap<>();


    public Game_Manager(Build_Manager build,Main main) {
        this.build = build;
        this.Red_Team = new Team(build);
        this.Blue_Team = new Team(build);
        this.main = main;
        this.scoreboard = new GameScoreboard(main,this);
    }

    private Build_Manager build;

    public void addToTeam(Player p, boolean toBlue) {

        if (toBlue) {
            Blue_Team.setRectangle(build.getBlueRect());
            Blue_Team.setSpawnLine((int) (build.getLoc().getX() + 3 * build.getWidth() / 8));
            Blue_Team.addPlayer(p);
        } else {
            Red_Team.setRectangle(build.getRedRect());
            Red_Team.setSpawnLine((int) (build.getLoc().getX() - 3 * build.getWidth() / 8));
            Red_Team.addPlayer(p);
        }

    }

    public boolean isPlayerInGame(Player p) {
        if (Red_Team.isPlayerInTeam(p) || Blue_Team.isPlayerInTeam(p))
            return true;
        return false;
    }

    public boolean isSameTeam(Player p1, Player p2) {
        if (!(Main.gamePhase == GamePhase.GAME_START)) {
            return false;

        }
        if (Red_Team.isPlayerInTeam(p1) && Red_Team.isPlayerInTeam(p2))
            return true;


        else if (Blue_Team.isPlayerInTeam(p1) && Blue_Team.isPlayerInTeam(p2))
            return true;

        return false;
    }

    public Rectangle getPlayerRectangle(Player p) {
        if (Blue_Team.isPlayerInTeam(p))
            return build.getBlueRect();
        else if (Red_Team.isPlayerInTeam(p))
            return build.getRedRect();
        return new Rectangle(0, 0, 0, 0);

    }

    public void respawn(Player p){
        if(Blue_Team.isPlayerInTeam(p)) {

            Blue_Team.spawnPoint(p);
        }
        else if(Red_Team.isPlayerInTeam(p)){

            Red_Team.spawnPoint(p);

        }


    }

    @EventHandler
    public void freeze(EntityDamageByEntityEvent e) {
        if (!(Main.gamePhase == GamePhase.GAME_START))
            return;

        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p1 = (Player) e.getDamager();
            Player p2 = (Player) e.getEntity();
            Location loc1 = p1.getLocation();
            Location loc2 = p2.getLocation();
            //both players in game
            if (!(isPlayerInGame(p1) && isPlayerInGame(p2)))
                return;

            //first not in game and second is in game
            if (!isPlayerInGame(p1) && isPlayerInGame(p2)) {
                e.setCancelled(true);
                return;
            }
            //first in game and second isn't
            if (isPlayerInGame(p1) && !isPlayerInGame(p2)) {
                e.setCancelled(true);
                return;
            }
            //both players are in different teams
            if (!isSameTeam(p1, p2)) {
                //make the hit player unhitable
                if (!hitablePlayers.containsKey(p2.getName())) {
                    hitablePlayers.put(p2.getName(), true);
                }
                //if the player is unhitable,return and cancel
                if (!hitablePlayers.get(p2.getName())) {
                    e.setCancelled(true);
                    return;
                }


                boolean damagerInRectangel = getPlayerRectangle(p1).contains(loc1.getX(), loc1.getZ());
                boolean damagedOutOfRectangel = !getPlayerRectangle(p2).contains(loc2.getX(), loc2.getZ());
                //the damager is on his side and the damaged isn't
                if (damagerInRectangel && damagedOutOfRectangel) {
                    //freeze method


                    frozenPlayers.put(p2.getName(), System.currentTimeMillis() / 1000 + main.config.getInt("freeze time"));
                    hitablePlayers.put(p2.getName(), false);
                    p2.damage(5);
                    new BukkitRunnable() {


                        @Override
                        public void run() {

                            hitablePlayers.replace(p2.getName(), true);


                        }
                    }.runTaskLater(Main.getPlugin(Main.class), main.config.getInt("freeze time") + main.config.getInt("resistance after freeze") * 20);


                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void freezeEvent(PlayerMoveEvent e) {
        if (!(Main.gamePhase == GamePhase.GAME_START))
            return;

        if (frozenPlayers.containsKey(e.getPlayer().getName())) {
            if (frozenPlayers.get(e.getPlayer().getName()) > System.currentTimeMillis() / 1000) {

                long timeleft = (frozenPlayers.get(e.getPlayer().getName()) - System.currentTimeMillis() / 1000);
                e.getPlayer().sendMessage(ChatColor.BLUE + "You can move in " + timeleft + " seconds!");
                e.setCancelled(true);
                return;

            }

        }


    }

    @EventHandler
    public void flagPlace(BlockPlaceEvent e) {
        if(!(Main.gamePhase == GamePhase.GAME_START))
            return;
        Player p = e.getPlayer();
        if (Red_Team.isPlayerInTeam(p) && e.getBlock().getType() == Material.BLUE_BANNER && e.getBlock().getLocation().equals(build.getRedTeamBlueFlagPlaceLoc())) {
            e.setCancelled(true);
            if(Red_Team.getTeamWins() == main.config.getInt("points-to-win")){
                for(Player online : Bukkit.getOnlinePlayers()){
                    online.sendTitle(ChatColor.GOLD + "The " + ChatColor.RED + "Red" + ChatColor.GOLD + "Team won!",ChatColor.YELLOW + "GG and thanks for playing!",10,5 * 20,10);
                }
                return;
            }
            Red_Team.addTeamWins();


            for (Player online : Bukkit.getOnlinePlayers()) {
                online.getInventory().remove(Material.BLUE_BANNER);
                online.sendTitle(ChatColor.GOLD + "The " + ChatColor.RED + "red" + ChatColor.GOLD + " team Scored!", "", 1, 40, 10);
            }
            for(Player teammate : Red_Team.getPlayers()){
                respawn(teammate);
                teammate.getInventory().clear();
                scoreboard.createScoreboard(teammate);
                teammate.setHealth(20);
            }
            for(Player otherTeam : Blue_Team.getPlayers()){
                respawn(otherTeam);
                otherTeam.getInventory().clear();
                scoreboard.createScoreboard(otherTeam);
                otherTeam.setHealth(20);
            }

            p.getWorld().getBlockAt(build.getRedFlagLoc()).setType(Material.RED_BANNER);
            p.getWorld().getBlockAt(build.getBlueFlagLoc()).setType(Material.BLUE_BANNER);
            //objective:regenerate the flags by spawnflag function in build manager;

        }
        if (Blue_Team.isPlayerInTeam(p) && e.getBlock().getType() == Material.RED_BANNER && e.getBlock().getLocation().equals(build.getBlueTeamRedFlagPlaceLoc())) {
            e.setCancelled(true);
            if(Blue_Team.getTeamWins() == main.config.getInt("points-to-win")){
                for(Player online : Bukkit.getOnlinePlayers()){
                    online.sendTitle(ChatColor.GOLD + "The " + ChatColor.BLUE + "Blue" + ChatColor.GOLD + "Team won!",ChatColor.YELLOW + "GG and thanks for playing!",10,5 * 20,10);
                }
                return;
            }
            Blue_Team.addTeamWins();


            for (Player online : Bukkit.getOnlinePlayers()) {

                online.sendTitle(ChatColor.GOLD + "The " + ChatColor.BLUE + "blue" + ChatColor.GOLD + " team Scored!", "", 1, 40, 10);

            }
            for(Player teammate : Blue_Team.getPlayers()){
                respawn(teammate);
                teammate.getInventory().clear();
                scoreboard.createScoreboard(teammate);
                teammate.setHealth(20);

            }
            for(Player otherTeam : Red_Team.getPlayers()){
                respawn(otherTeam);
                otherTeam.getInventory().clear();
                scoreboard.createScoreboard(otherTeam);
                otherTeam.setHealth(20);
            }
                

            p.getWorld().getBlockAt(build.getRedFlagLoc()).setType(Material.RED_BANNER);
            p.getWorld().getBlockAt(build.getBlueFlagLoc()).setType(Material.BLUE_BANNER);


        }
    }
    @EventHandler
    public void foodKeep(FoodLevelChangeEvent e){
        if(main.gamePhase == GamePhase.GAME_START){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void breakDefender(BlockBreakEvent e){
        if(!(main.gamePhase == GamePhase.BEFORE_DRAW || main.gamePhase == GamePhase.GAME_END)) {
            if (!e.getBlock().getType().name().toUpperCase().contains("BANNER")) {
                e.getPlayer().sendMessage(ChatColor.DARK_RED + "Please don't destroy anything!");
                e.setCancelled(true);

            }
        }
    }
    @EventHandler
    public void respawner(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(isPlayerInGame(p)){
            respawn(p);
        }
    }

}

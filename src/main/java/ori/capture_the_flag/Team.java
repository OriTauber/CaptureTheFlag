package ori.capture_the_flag;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ori.capture_the_flag.commands.Build_Manager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Team {
    public List<Player> getPlayers() {
        return players;
    }

    private List<Player> players;
    private int spawnLine;

    public int getTeamWins() {
        return teamWins;
    }

    public void setTeamWins(int teamWins) {
        this.teamWins = teamWins;
    }

    public void addTeamWins() {
        this.teamWins++;
    }

    private int teamWins = 0;

    public Team(Build_Manager build) {
        this.players = new ArrayList<Player>();
        this.build = build;
    }



    private Build_Manager build;





    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }


    private Rectangle rectangle;
    public void spawnPoint(Player p){
        Location tpLoc = new Location(build.getLoc().getWorld(),build.getLoc().getX(),build.getLoc().getY(),build.getLoc().getZ());


        Random r = new Random();
        int rand_z = (int) (r.nextInt((int) rectangle.getHeight() - 1) + rectangle.getMinY());
        rand_z = (int) Math.max(rand_z,rectangle.getMinY() + 1);
        tpLoc.setX(spawnLine);
        tpLoc.setZ(rand_z);
        tpLoc.setY(build.getLoc().getY() + 1);
        p.teleport(tpLoc);


    }

    public void addPlayer(Player p) {



        players.add(p);
        spawnPoint(p);


    }
    public void setSpawnLine(int spawnLine){
        this.spawnLine = spawnLine;

    }
    public boolean isPlayerInTeam(Player p){
        for(Player player : this.players){
            if(p == player)
                return true;

        }
        return false;
    }

}

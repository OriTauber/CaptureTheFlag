package ori.capture_the_flag.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import ori.capture_the_flag.GamePhase;
import ori.capture_the_flag.Main;

import java.awt.*;

public class Build_Manager {

    public Location getLoc() {
        return loc;
    }

    private Location loc = null;

    public int getWidth() {
        return width;
    }

    private int width;

    public int getHeight() {
        return height;
    }

    private int height;

    public Rectangle getBlueRect() {
        return blueRect;
    }

    public Rectangle getRedRect() {
        return redRect;
    }

    private Rectangle blueRect;
    private Rectangle redRect;
    private Location redTeamBlueFlagPlaceLoc;

    public Location getRedTeamBlueFlagPlaceLoc() {
        return redTeamBlueFlagPlaceLoc;
    }

    public Location getBlueTeamRedFlagPlaceLoc() {
        return blueTeamRedFlagPlaceLoc;
    }

    private Location blueTeamRedFlagPlaceLoc;


    public Location getBlueFlagLoc() {
        return blueFlagLoc;
    }

    public Location getRedFlagLoc() {
        return redFlagLoc;
    }

    private Location blueFlagLoc;
    private Location redFlagLoc;




    public void draw_Walls(Location loc, int width, int height, int tall, Player p){
        



        this.width = width;

        this.height = height;
        Location drawloc = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());
        this.loc = loc;


        int initX = (int) loc.getX();
        int initZ = (int) loc.getZ();
        int initY = (int) loc.getY();

        for (int x = initX - width / 2; x < initX + width / 2; x++){

            for (int y =0;y < tall;y++){
                drawloc.setX(x);
                drawloc.setY(initY + y);
                drawloc.setZ(initZ + height/2);


                p.getWorld().getBlockAt(drawloc).setType(Material.CYAN_WOOL);
                drawloc.setZ(initZ - height/2);
                p.getWorld().getBlockAt(drawloc).setType(Material.CYAN_WOOL);




            }

        }
        for (int z = initZ - height / 2; z < initZ + height / 2; z++){
            for (int y =0;y < tall;y++) {

                drawloc.setY(initY + y);
                drawloc.setZ(z);
                drawloc.setX(initX + width / 2);


                p.getWorld().getBlockAt(drawloc).setType(Material.CYAN_WOOL);
                drawloc.setX(initX - width / 2);
                p.getWorld().getBlockAt(drawloc).setType(Material.CYAN_WOOL);


            }
        }
        for (int x = initX - width / 2 + 1; x < initX + width / 2 ; x++){
            for (int z = initZ - height / 2 + 1; z < initZ + height / 2 ; z++){
                drawloc.setY(initY - 1);

                drawloc.setX(x);
                drawloc.setZ(z);
                if(x < initX) {

                    p.getWorld().getBlockAt(drawloc).setType(Material.RED_WOOL);
                }
                else if(x > initX) {
                    p.getWorld().getBlockAt(drawloc).setType(Material.BLUE_WOOL);

                } else {
                    p.getWorld().getBlockAt(drawloc).setType(Material.BLACK_WOOL);

                }


            }
        }
        //height = width(swap)
        this.blueRect = new Rectangle(initX,initZ - height / 2,width / 2,height);
        this.redRect = new Rectangle(initX - width / 2,initZ - height / 2,width / 2,height);
        //red natural flag place
        p.getWorld().getBlockAt(initX - width / 2 + 3,initY - 1,initZ).setType(Material.RED_CONCRETE);
        //blue natural flag place
        p.getWorld().getBlockAt(initX + width / 2 - 3,initY - 1,initZ).setType(Material.BLUE_CONCRETE);
        //red's bringback loc
        p.getWorld().getBlockAt(initX - width / 2 + 3,initY - 1,initZ - 1).setType(Material.BLUE_CONCRETE);
        //blue's bringback loc
        p.getWorld().getBlockAt(initX + width / 2 - 3,initY - 1,initZ - 1).setType(Material.RED_CONCRETE);

        this.redTeamBlueFlagPlaceLoc = new Location(p.getWorld(),initX - width / 2 + 3,initY ,initZ - 1);
        this.blueTeamRedFlagPlaceLoc = new Location(p.getWorld(),initX + width / 2 - 3,initY ,initZ - 1);

        this.redFlagLoc = new Location(p.getWorld(),initX - width / 2 + 3,initY ,initZ);

        p.getWorld().getBlockAt(redFlagLoc).setType(Material.RED_BANNER);

        Rotatable data = (Rotatable) p.getWorld().getBlockAt(initX - width / 2 + 3,initY ,initZ ).getBlockData();
        data.setRotation(BlockFace.EAST);
        p.getWorld().getBlockAt(initX - width / 2 + 3,initY ,initZ ).setBlockData(data);

        this.blueFlagLoc = new Location(p.getWorld(),initX + width / 2 - 3,initY ,initZ);

        p.getWorld().getBlockAt(blueFlagLoc).setType(Material.BLUE_BANNER);

        Rotatable data2 = (Rotatable) p.getWorld().getBlockAt(initX + width / 2 - 3,initY ,initZ ).getBlockData();
        data2.setRotation(BlockFace.WEST);

        p.getWorld().getBlockAt(initX + width / 2 - 3,initY ,initZ ).setBlockData(data2);





        Main.gamePhase = GamePhase.AFTER_DRAW;






    }

}

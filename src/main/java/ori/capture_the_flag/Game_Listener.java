package ori.capture_the_flag;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import ori.capture_the_flag.commands.Build_Manager;

public class Game_Listener implements Listener {
    public Game_Listener(Build_Manager build) {
        this.build = build;
    }

    private Build_Manager build;
    public Game_Listener(){

    }


    @EventHandler
    public void FlagBreak(BlockBreakEvent event){



        Player player = event.getPlayer();


        if(event.getBlock().getType() == Material.RED_BANNER){

            event.setDropItems(false);

            player.getInventory().addItem(new ItemStack(Material.RED_BANNER));
        }
        if(event.getBlock().getType() == Material.BLUE_BANNER){

            event.setDropItems(false);

            player.getInventory().addItem(new ItemStack(Material.BLUE_BANNER));
        }


    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){

//        if(Bukkit.getOnlinePlayers().size() == 1){
//            build.draw_Walls(e.getPlayer().getLocation(),30,30,5, e.getPlayer());
//
//        }
//        else if(Bukkit.getOnlinePlayers().size() > 1){
//            Player p = e.getPlayer();
//            p.getLocation().setX(build.getLoc().getX());
//        }

    }


}

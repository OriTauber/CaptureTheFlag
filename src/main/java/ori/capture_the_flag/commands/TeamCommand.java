package ori.capture_the_flag.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ori.capture_the_flag.Game_Manager;
import ori.capture_the_flag.Main;

import java.util.HashMap;

public class TeamCommand implements Listener {
    private Game_Manager game;
    private Build_Manager build;
    private Main main;


    public TeamCommand(Game_Manager game,Build_Manager build,Main main) {
        this.game = game;
        this.build = build;
        this.main = main;


    }

    Inventory inv;





    public void teamCommand(Player player){
        inv = Bukkit.createInventory(null,9, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Select inventory");
        inv.setItem(5,new ItemStack(Material.BLUE_WOOL));
        inv.setItem(3,new ItemStack(Material.RED_WOOL));
        player.openInventory(inv);

    }
    @EventHandler
    public void inventoryClick(InventoryClickEvent e){

        if(e.getView().getTitle().contains("Select inventory")){
           if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.RED_WOOL){

               Player p = (Player) e.getWhoClicked();
               p.sendMessage("You selected the red team");
               main.getCanJoinTeamMap().put(p.getUniqueId(),false);
               game.addToTeam(p,false);
               p.sendMessage(build.getLoc().toString());




               e.setCancelled(true);



            }
           else if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BLUE_WOOL){
               Player p = (Player) e.getWhoClicked();
               p.sendMessage("You selected the blue team");
               main.getCanJoinTeamMap().put(p.getUniqueId(),false);

               game.addToTeam(p,true);
               e.setCancelled(true);


           }
        }
    }

}

package me.exodian.maskplugin;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import com.codingforcookies.armorequip.ArmorListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;


public class MaskPlugin extends JavaPlugin {

    public Manager m = new Manager();

    @Override
    public void onEnable() {

        getLogger().info("MaskPlugin has started.");

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(m, this);
        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);

        m.setTeamName(getConfig().getString("scoreboardTeam.name"));

        m.prepareScoreboardTeam();


    }


    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Mask")) {
            Player p = (Player) sender;
            if (p.isOp()) {
                p.getInventory().addItem(m.getInvisHelm());
            }
            return true;
        }
        return false;
    }


    class Manager implements Listener {


        private String teamName;
        private ItemStack InvisHelm;

        public void prepareScoreboardTeam() {
            final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

            prepareArmor();

            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            team.setDisplayName(getConfig().getString("scoreboardTeam.displayName"));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);


        }

        public void setTeamName(String s) {
            teamName = s;
        }

        public ItemStack getInvisHelm() {
            return InvisHelm;
        }

        public void prepareArmor() {
            ItemStack InvisHelmTEMP = new ItemStack(Material.SKELETON_SKULL);
            ItemMeta InvisHelmMETA = InvisHelmTEMP.getItemMeta();
            InvisHelmMETA.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Helm of Invisibility");
            InvisHelmMETA.setLocalizedName("InvisHelm");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add("A holy remnant of the Ancient One.");
            InvisHelmMETA.setLore(lore);
            InvisHelmTEMP.setItemMeta(InvisHelmMETA);
            InvisHelm = InvisHelmTEMP;
        }

        @EventHandler(priority = EventPriority.HIGH)
        public void onArmorUsage(ArmorEquipEvent event) {
            Player p = event.getPlayer();
            final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            try {
                if (event.getOldArmorPiece().equals(InvisHelm)) {
                    if (scoreboard.getTeam(teamName).hasEntry(p.getName())) {
                        scoreboard.getTeam(teamName).removeEntry(p.getName());
                        p.sendMessage(ChatColor.DARK_RED + "[InvisHelm] Your name-tag is unHidden!");
                    }
                } else if (event.getNewArmorPiece().equals(InvisHelm)) {
                    scoreboard.getTeam(teamName).addEntry(p.getName());
                    p.sendMessage(ChatColor.DARK_RED + "[InvisHelm] Your name-tag is Hidden!");
                }
            }
            catch(Exception e){
                p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "[InvisHelm] Error. Try Equipping the Helm through your inventory.");
                System.out.print(e.toString());
            }


        }

    }

/**  ------OLD WAY OF GIVING YOU THE HELM------
 @EventHandler(priority = EventPriority.HIGH)
 public void onPlayerUse(PlayerInteractEvent event) {
 Player p = event.getPlayer();
 if (p.isOp() && event.getAction().equals(event.getAction().RIGHT_CLICK_AIR)) {
 if (p.getItemInHand().getType() == Material.BLAZE_POWDER) {
 p.getInventory().addItem(InvisHelm);
 }
 }
 }
 **/


}

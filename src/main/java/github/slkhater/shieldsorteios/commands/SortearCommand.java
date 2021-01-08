package github.slkhater.shieldsorteios.commands;

import github.slkhater.shieldsorteios.Core;
import github.slkhater.shieldutils.api.ColorsAPI;
import github.slkhater.shieldutils.enums.MessagesEnum;
import github.slkhater.shieldutils.enums.PermissionsEnum;
import github.slkhater.shieldutils.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SortearCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            FileConfiguration config = Core.getInstance().getConfig();
            if (cmd.getName().equalsIgnoreCase("sortear") && player.hasPermission(PermissionsEnum.SORTEAR.toString())) MessageManager.goodPlayer(player, "&fUm sorteio randômico foi iniciado com sucesso.");
            else MessageManager.errorPlayer(player, MessagesEnum.NO_PERMISSION.toString());
            for(Player online : Bukkit.getOnlinePlayers()) online.sendTitle(ColorsAPI.colorize(config.getString("titles.sorteando-linha1")), ColorsAPI.colorize(config.getString("titles.sorteando-linha2")));
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                Player sorteado = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
                if(sorteado!=null){
                    Set<String> keys = config.getConfigurationSection("recompensas").getKeys(false);
                    String randomkey = (String) keys.toArray()[new Random().nextInt(keys.size())];
                    for(Player p : Bukkit.getOnlinePlayers()) p.sendTitle(ColorsAPI.colorize(config.getString("titles.sorteado-linha1").replace("{jogador}", sorteado.getName())), ColorsAPI.colorize(config.getString("titles.sorteado-linha2").replace("{premio}", config.getString("recompensas."+randomkey+".Premio"))));
                    if(sorteado.getInventory().firstEmpty() != -1){
                        ItemStack premioIS = new ItemStack(Material.getMaterial(config.getInt("recompensas."+randomkey+".Item.ID")));
                        ItemMeta premioISMeta = premioIS.getItemMeta();
                        premioISMeta.setDisplayName(ColorsAPI.colorize(config.getString("recompensas."+randomkey+".Item.Nome")));
                        List<String> lore = new ArrayList<>(Arrays.asList(config.getString("recompensas."+randomkey+".Item.Lore").replace('&', '§').split("\n")));
                        premioISMeta.setLore(lore);
                        premioIS.setItemMeta(premioISMeta);
                        sorteado.getInventory().addItem(premioIS);
                    }else MessageManager.errorPlayer(sorteado, "Você perdeu a Recompensa porque tinha o inventário cheio.");
                }else System.out.println("[ShieldSorteios] An error occurred while trying to perform a action!");
            }, 5*20L);
            return true;
        }else System.out.println("[ShieldSorteios] An error occurred while trying to perform a action!");
        return true;
    }
}

package github.slkhater.shieldsorteios.commands;

import github.slkhater.shieldsorteios.Core;
import github.slkhater.shieldutils.api.ColorsAPI;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SortearCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            FileConfiguration config = Core.getInstance().getConfig();
            if (cmd.getName().equalsIgnoreCase("sortear") && player.hasPermission(PermissionsEnum.SORTEAR.toString()))
                if(args.length == 0) MessageManager.infoPlayer(player, "&7&oArgumentos disponíveis: (iniciar|reload)");
                else if(args[0].equalsIgnoreCase("iniciar")){
                    MessageManager.goodPlayer(player, "Um sorteio randômico foi iniciado com sucesso.");
                    for(Player online : Bukkit.getOnlinePlayers()) online.sendTitle(ColorsAPI.colorize(config.getString("titles.sorteando-linha1")), ColorsAPI.colorize(config.getString("titles.sorteando-linha2")));
                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                        Player sorteado = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);
                        if(sorteado!=null){
                            Set<String> keys = config.getConfigurationSection("sorteio.recompensas").getKeys(false);
                            String randomKey = (String) keys.toArray()[new Random().nextInt(keys.size())];
                            for(Player p : Bukkit.getOnlinePlayers()) p.sendTitle(ColorsAPI.colorize(config.getString("titles.sorteado-linha1").replace("{jogador}", sorteado.getName())), ColorsAPI.colorize(config.getString("titles.sorteado-linha2").replace("{premio}", config.getString("sorteio.recompensas."+randomKey+".NomePremio"))));
                            for(String item : config.getConfigurationSection("sorteio.recompensas."+randomKey+".Items").getKeys(false)){
                                if(sorteado.getInventory().firstEmpty() != -1){
                                    ItemStack premioIS = new ItemStack(Material.getMaterial(config.getInt("sorteio.recompensas."+randomKey+".Items."+item+".ID")), config.getInt("sorteio.recompensas."+randomKey+".Items."+item+".Quantidade"));
                                    ItemMeta premioISMeta = premioIS.getItemMeta();
                                    premioISMeta.setDisplayName(ColorsAPI.colorize(config.getString("sorteio.recompensas."+randomKey+".Items."+item+".Nome")));
                                    List<String> lore = new ArrayList<>(Arrays.asList(config.getString("sorteio.recompensas."+randomKey+".Items."+item+".Lore").replace('&', '§').split("\n")));
                                    premioISMeta.setLore(lore);
                                    premioIS.setItemMeta(premioISMeta);
                                    sorteado.getInventory().addItem(premioIS);
                                }else MessageManager.errorPlayer(sorteado, "Você perdeu uma Recompensa porque seu inventário está cheio.");
                            }
                            for(String comando : config.getStringList("sorteio.recompensas."+randomKey+".Comandos")) Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), comando.replace("{jogador}", sorteado.getName()));
                        }else System.out.println("[ShieldSorteios] An error occurred while trying to perform a action!");
                    }, config.getInt("sorteio.tempo")*20L);
                } else if(args[0].equalsIgnoreCase("reload")) MessageManager.goodPlayer(player, "Reinicialização da configuração efetuada com sucesso! &8&o("+JavaPlugin.getProvidingPlugin(this.getClass()).getName()+")"); Core.getInstance().reloadConfig();
                return true;
        }else System.out.println("[ShieldSorteios] An error occurred while trying to perform a action!");
        return true;
    }
}

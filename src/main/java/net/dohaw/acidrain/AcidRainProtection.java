package net.dohaw.acidrain;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class AcidRainProtection extends Enchantment {

    public AcidRainProtection() {
        super(101);
    }

    @Override
    public String getName() {
        return "Acid Rain Protection";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_HEAD;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        String itemName = item.getType().toString().toLowerCase();
        return itemName.contains("helmet") || itemName.contains("head");
    }

}

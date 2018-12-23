package net.neferett.linaris.rush.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {
    private String title;
    private final int amount;
    private final short damage;
    private final Material material;
    private final List<String> lores = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(final ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
    }

    public ItemBuilder(final Material material) {
        this(material, 1, (short) 0);
    }

    public ItemBuilder(final Material material, final int amount) {
        this(material, amount, (short) 0);
    }

    public ItemBuilder(final Material material, final int amount, final short damage) {
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    public ItemBuilder(final Material material, final short durability) {
        this(material, 1, durability);
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder addLores(final String... lores) {
        this.lores.addAll(Arrays.asList(lores));
        return this;
    }

    public ItemStack build() {
        if (this.material == null) throw new NullPointerException("Material cannot be null!");
        final ItemStack item = new ItemStack(this.material, this.amount, this.damage);
        if (!this.enchantments.isEmpty()) {
            item.addUnsafeEnchantments(this.enchantments);
        }
        final ItemMeta meta = item.getItemMeta();
        if (this.title != null) {
            meta.setDisplayName(this.title);
        }
        if (!this.lores.isEmpty()) {
            meta.setLore(this.lores);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setTitle(final String title) {
        this.title = title;
        return this;
    }
}

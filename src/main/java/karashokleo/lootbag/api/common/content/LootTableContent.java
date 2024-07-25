package karashokleo.lootbag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class LootTableContent extends StacksContent
{
    public static final Codec<LootTableContent> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Identifier.CODEC.fieldOf("id").forGetter(LootTableContent::getId)
            ).and(contentFields(ins)).apply(ins, LootTableContent::new)
    );

    public static final ContentType<LootTableContent> TYPE = new ContentType<>(CODEC);

    protected final Identifier id;

    public LootTableContent(Identifier id, Icon icon, int descriptionLines)
    {
        super(icon, descriptionLines);
        this.id = id;
    }

    public Identifier getId()
    {
        return id;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE;
    }

    @Override
    protected List<ItemStack> getLootStacks(ServerPlayerEntity player)
    {
        if (id.equals(LootTables.EMPTY)) return Collections.emptyList();
        ServerWorld world = player.getServerWorld();
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.ORIGIN, player.getPos())
                .build(LootContextTypes.CHEST);
        LootTable lootTable = world.getServer().getLootManager().getLootTable(id);
        return lootTable.generateLoot(lootContextParameterSet);
    }
}

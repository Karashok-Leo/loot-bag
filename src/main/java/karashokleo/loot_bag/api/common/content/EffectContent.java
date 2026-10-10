package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class EffectContent extends Content
{
    public static final MapCodec<EffectContent> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    Effect.CODEC.listOf().fieldOf("effects").forGetter(EffectContent::getEffects)
            ).and(contentFields(ins).t1()).apply(ins, EffectContent::new)
    );

    public static final DeferredHolder<ContentType<?>, ContentType<EffectContent>> TYPE = LootBagRegistry.EFFECT_CONTENT;

    protected final List<Effect> effects;

    public EffectContent(List<Effect> effects, Icon icon)
    {
        super(icon);
        this.effects = effects;
    }

    public List<Effect> getEffects()
    {
        return effects;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE.get();
    }

    @Override
    public void reward(ServerPlayer player)
    {
        for (Effect effect : this.effects)
            player.addEffect(effect.getInstance());
    }

    public record Effect(
            Holder<MobEffect> type,
            int duration,
            int amplifier,
            boolean ambient,
            boolean showParticles,
            boolean showIcon
    )
    {
        public static final Codec<Effect> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("type").forGetter(Effect::type),
                        Codec.INT.optionalFieldOf("duration", 20).forGetter(Effect::duration),
                        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(Effect::amplifier),
                        Codec.BOOL.optionalFieldOf("ambient", false).forGetter(Effect::ambient),
                        Codec.BOOL.optionalFieldOf("showParticles", true).forGetter(Effect::showParticles),
                        Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(Effect::showIcon)
                ).apply(ins, Effect::new)
        );
        public static final int DEFAULT_DURATION = 20;

        public Effect(Holder<MobEffect> type)
        {
            this(type, DEFAULT_DURATION);
        }

        public Effect(Holder<MobEffect> type, int duration)
        {
            this(type, duration, 0);
        }

        public Effect(Holder<MobEffect> type, int duration, int amplifier)
        {
            this(type, duration, amplifier, false, true);
        }

        public Effect(Holder<MobEffect> type, int duration, int amplifier, boolean ambient, boolean visible)
        {
            this(type, duration, amplifier, ambient, visible, visible);
        }

        public MobEffectInstance getInstance()
        {
            return new MobEffectInstance(type, duration, amplifier, ambient, showParticles, showIcon);
        }
    }
}

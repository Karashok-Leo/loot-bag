package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class EffectContent extends Content
{
    public static final Codec<EffectContent> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Effect.CODEC.listOf().fieldOf("effects").forGetter(EffectContent::getEffects)
            ).and(contentFields(ins).t1()).apply(ins, EffectContent::new)
    );

    public static final ContentType<EffectContent> TYPE = new ContentType<>(CODEC);

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
        return TYPE;
    }

    @Override
    public void reward(ServerPlayerEntity player)
    {
        for (Effect effect : this.effects)
            player.addStatusEffect(effect.getInstance());
    }

    public record Effect(
            StatusEffect type,
            int duration,
            int amplifier,
            boolean ambient,
            boolean showParticles,
            boolean showIcon
    )
    {
        public static final Codec<Effect> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        Registries.STATUS_EFFECT.getCodec().fieldOf("type").forGetter(Effect::type),
                        Codec.INT.optionalFieldOf("duration", 20).forGetter(Effect::duration),
                        Codec.INT.optionalFieldOf("amplifier", 0).forGetter(Effect::amplifier),
                        Codec.BOOL.optionalFieldOf("ambient", false).forGetter(Effect::ambient),
                        Codec.BOOL.optionalFieldOf("showParticles", true).forGetter(Effect::showParticles),
                        Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(Effect::showIcon)
                ).apply(ins, Effect::new)
        );
        public static final int DEFAULT_DURATION = 20;

        public Effect(StatusEffect type)
        {
            this(type, DEFAULT_DURATION);
        }

        public Effect(StatusEffect type, int duration)
        {
            this(type, duration, 0);
        }

        public Effect(StatusEffect type, int duration, int amplifier)
        {
            this(type, duration, amplifier, false, true);
        }

        public Effect(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible)
        {
            this(type, duration, amplifier, ambient, visible, visible);
        }

        public StatusEffectInstance getInstance()
        {
            return new StatusEffectInstance(type, duration, amplifier, ambient, showParticles, showIcon);
        }
    }
}

package net.pedroricardo.content.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.util.stream.Stream;

public class TheAppleEndBiomeSource extends BiomeSource {
    public static final MapCodec<TheAppleEndBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Biome.REGISTRY_CODEC.fieldOf("center_biome").forGetter(source -> source.centerBiome), Biome.REGISTRY_CODEC.fieldOf("highlands_biome").forGetter(source -> source.highlandsBiome), Biome.REGISTRY_CODEC.fieldOf("midlands_biome").forGetter(source -> source.midlandsBiome), Biome.REGISTRY_CODEC.fieldOf("small_islands_biome").forGetter(source -> source.smallIslandsBiome), Biome.REGISTRY_CODEC.fieldOf("barrens_biome").forGetter(source -> source.barrensBiome)).apply(instance, instance.stable(TheAppleEndBiomeSource::new)));
    private final RegistryEntry<Biome> centerBiome;
    private final RegistryEntry<Biome> highlandsBiome;
    private final RegistryEntry<Biome> midlandsBiome;
    private final RegistryEntry<Biome> smallIslandsBiome;
    private final RegistryEntry<Biome> barrensBiome;

    public TheAppleEndBiomeSource(RegistryEntry<Biome> centerBiome, RegistryEntry<Biome> highlandsBiome, RegistryEntry<Biome> midlandsBiome, RegistryEntry<Biome> smallIslandsBiome, RegistryEntry<Biome> barrensBiome) {
        this.centerBiome = centerBiome;
        this.highlandsBiome = highlandsBiome;
        this.midlandsBiome = midlandsBiome;
        this.smallIslandsBiome = smallIslandsBiome;
        this.barrensBiome = barrensBiome;
    }

    @Override
    public Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.of(this.centerBiome, this.highlandsBiome, this.midlandsBiome, this.smallIslandsBiome, this.barrensBiome);
    }

    @Override
    public MapCodec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        int m;
        int i = BiomeCoords.toBlock(x);
        int j = BiomeCoords.toBlock(y);
        int k = BiomeCoords.toBlock(z);
        int l = ChunkSectionPos.getSectionCoord(i);
        if ((long)l * (long)l + (long)(m = ChunkSectionPos.getSectionCoord(k)) * (long)m <= 4096L) {
            return this.centerBiome;
        }
        int n = (ChunkSectionPos.getSectionCoord(i) * 2 + 1) * 8;
        int o = (ChunkSectionPos.getSectionCoord(k) * 2 + 1) * 8;
        double d = noise.erosion().sample(new DensityFunction.UnblendedNoisePos(n, j, o));
        if (d > 0.25) {
            return this.highlandsBiome;
        }
        if (d >= -0.0625) {
            return this.midlandsBiome;
        }
        if (d < -0.21875) {
            return this.smallIslandsBiome;
        }
        return this.barrensBiome;
    }
}

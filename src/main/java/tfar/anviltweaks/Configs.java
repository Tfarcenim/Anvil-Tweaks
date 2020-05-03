package tfar.anviltweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configs {

  public static final ServerConfig SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class ServerConfig {

    public static ForgeConfigSpec.IntValue repair_cost_cap;
    public static ForgeConfigSpec.BooleanValue prior_work_penalty;
    public static ForgeConfigSpec.BooleanValue damageable;
    public static ForgeConfigSpec.DoubleValue damage_chance;
    public static ForgeConfigSpec.BooleanValue cheap_renaming;
    public static ForgeConfigSpec.DoubleValue prior_work_penalty_scale;

    ServerConfig(ForgeConfigSpec.Builder builder) {

      builder.push("general");
      repair_cost_cap = builder
              .comment("Repair cost cap")
              .translation("text.anviltweaks.config.repair_cost_cap")
              .defineInRange("repair_cost_cap",40,1,Integer.MAX_VALUE);
      prior_work_penalty = builder
              .comment("Prior Work Penalty")
              .translation("text.anviltweaks.config.prior_work_penalty")
              .define("prior_work_penalty",true);
      prior_work_penalty_scale = builder
              .comment("Prior Work Penalty Scale")
              .translation("text.anviltweaks.config.prior_work_penalty_scale")
              .defineInRange("prior_work_penalty_scale",2,1,Double.MAX_VALUE);
      damageable = builder
              .comment("Does anvil take damage")
              .translation("text.anviltweaks.config.damageable")
              .define("damageable",true);
      damage_chance = builder
              .comment("Damage chance")
              .translation("text.anviltweaks.config.damage_chance")
              .defineInRange("damage_chance",.12,0,1);
      cheap_renaming = builder
              .comment("Anvil renaming always costs 1 level")
              .translation("text.anviltweaks.config.cheap_renaming")
              .define("cheap_renaming",false);
      builder.pop();
    }
  }
}

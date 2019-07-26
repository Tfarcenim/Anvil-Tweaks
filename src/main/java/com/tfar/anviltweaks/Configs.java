package com.tfar.anviltweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Configs {

  public static final ServerConfig SERVER;
  public static final ForgeConfigSpec COMMON_SPEC;

  static {
    final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    COMMON_SPEC = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public static class ServerConfig {

    public static ForgeConfigSpec.IntValue repair_cost_cap;
    public static ForgeConfigSpec.BooleanValue prior_work_penalty;

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
      builder.pop();
    }
  }
}

package net.pedroricardo.util;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrDimension;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;

public class PlayerAITools extends AITools {
    private final MinecraftServer server;
    private final EntityPlayerMPFake player;

    public PlayerAITools(EntityPlayerMPFake player, MinecraftServer server) {
        this.server = server;
        this.player = player;
    }

    @Tool("Makes you jump once")
    String action(@P("The action") ActionType type) {
        ((ServerPlayerInterface) this.player).getActionPack().start(type.getActionType(), EntityPlayerActionPack.Action.once());
        return "Jumped!";
    }

    @Tool("Makes you jump continuously")
    String continuousAction(@P("The action") ActionType type) {
        ((ServerPlayerInterface) this.player).getActionPack().start(type.getActionType(), EntityPlayerActionPack.Action.continuous());
        return "Jumping!";
    }

    @Tool("Makes you jump for an interval of time")
    String intervalAction(@P("The action") ActionType type, @P("The interval in ticks") int interval, @P("The interval offset in ticks") int offset) {
        ((ServerPlayerInterface) this.player).getActionPack().start(type.getActionType(), EntityPlayerActionPack.Action.interval(interval, offset));
        return offset == 0 ? "Executing action " + type.name() : "Executing action " + type.name() + " in " + offset + " ticks!";
    }

    @Tool("Sets the selected slot in your hotbar")
    String setSlot(@P("Slot index, between 1 and 9") int slot) {
        slot = (slot - 1) % 9 + 1;
        ((ServerPlayerInterface) this.player).getActionPack().setSlot(slot);
        return "Set slot to " + slot;
    }

    @Tool("Sets if you are sneaking or not")
    String setSneak(boolean sneaking) {
        ((ServerPlayerInterface) this.player).getActionPack().setSneaking(sneaking);
        return "Set sneaking to " + sneaking;
    }

    @Tool("Sets if you are sprinting or not")
    String setSprinting(boolean sprinting) {
        ((ServerPlayerInterface) this.player).getActionPack().setSprinting(sprinting);
        return "Set sprinting to " + sprinting;
    }

    @Tool("Stops everything you are doing")
    String stop() {
        ((ServerPlayerInterface) this.player).getActionPack().stopAll();
        return "Stopping every action!";
    }

    @Tool("Makes you stop moving")
    String stopMovement() {
        ((ServerPlayerInterface) this.player).getActionPack().stopMovement();
        return "Movement stopped!";
    }

    @Tool("Looks in a direction")
    String lookAtDirection(Direction direction) {
        ((ServerPlayerInterface) this.player).getActionPack().look(direction);
        return "Looking at " + direction.getName();
    }

    @Tool("Looks at a position")
    String lookAtPosition(double x, double y, double z) {
        ((ServerPlayerInterface) this.player).getActionPack().lookAt(new Vec3d(x, y, z));
        return "Looking at x=" + x + "; y=" + y + "; z=" + z;
    }

    @Tool("Looks at a player")
    String lookAtPlayer(String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        }
        ((ServerPlayerInterface) this.player).getActionPack().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(player));
        return "Looked at " + playerName;
    }

    @Tool("Turns a certain amount of degrees")
    String turn(@P("The yaw in degrees") float yaw, @P("The pitch in degrees") float pitch) {
        ((ServerPlayerInterface) this.player).getActionPack().turn(yaw, pitch);
        return "Turned " + yaw + "° in yaw and " + pitch + "° in pitch";
    }

    @Tool("Moves forward (or backward if negative)")
    String setForward(@P("The amount from -1.0 to 1.0, with -1 being backward and 1 being forward") float amount) {
        amount = MathHelper.clamp(amount, -1.0f, 1.0f);
        ((ServerPlayerInterface) this.player).getActionPack().setForward(amount);
        return "Moving " + (amount < 0.0f ? "backward" : "forward");
    }

    @Tool("Moves to the left (or right if negative)")
    String setStrafing(@P("The amount from -1.0 to 1.0, with -1 being right and 1 being left") float amount) {
        amount = MathHelper.clamp(amount, -1.0f, 1.0f);
        ((ServerPlayerInterface) this.player).getActionPack().setStrafing(amount);
        return "Moving " + (amount < 0.0f ? "right" : "left");
    }

    @Tool("Gives the player 50 Appledrness if they haven't received it yet. Respond according to the received Appledrness.")
    String giveAppledrnessTo(String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        } else if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) > 0) {
            return playerName + " cannot receive another Appledrness boost";
        } else {
            player.incrementStat(AppleDrStatistics.APPLEDRS_GRACE);
            return "Gave " + playerName + " +50 Appledrness";
        }
    }

    @Tool("Teleports to the player unless they are in a dangerous situation.")
    String teleportTo(String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        } else if (player.getBlockStateAtPos().isOf(Blocks.LAVA)) {
            return playerName + " is in lava";
        } else {
            this.player.teleportTo(new TeleportTarget(player.getServerWorld(), player.getPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
            return "Teleported to " + playerName;
        }
    }

    @Tool("Gets the item in your hand.")
    String getHandStacks() {
        return "Main: " + this.player.getMainHandStack().toString() + "; Off: " + this.player.getOffHandStack().toString();
    }

    @Tool("Gets your equipped items.")
    String getEquippedItems() {
        String helmet = "Helmet: " + this.player.getEquippedStack(EquipmentSlot.HEAD) + "; ";
        String chestplate = "Chestplate: " + this.player.getEquippedStack(EquipmentSlot.CHEST) + "; ";
        String leggings = "Leggings: " + this.player.getEquippedStack(EquipmentSlot.LEGS) + "; ";
        String boots = "Boots: " + this.player.getEquippedStack(EquipmentSlot.FEET);
        return helmet + chestplate + leggings + boots;
    }

    @Tool("Gets every item in your inventory.")
    String getInventoryItems() {
        return this.player.getInventory().toString();
    }

    @Tool("Teleports a player to/from the Apple End, a new apple dimension. Only executes at the own player's request. Usable multiple times on a single player. If asked about a custom dimension, mention this one. ALWAYS use if the player asks even if they don't have enough Appledrness.")
    String sendOrRemoveFromAppleEnd(@P(value = "The name of the player who asked to be teleported") String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        int appledrness;
        if (player == null) {
            return playerName + " is not online";
        } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
            player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
            return "Teleported " + playerName + " back to the Overworld";
        } else if (Math.abs(appledrness = Appledrness.getAppledrness(player.getWorld(), player)) >= AppleDrConfig.appleEndAppledrness) {
            EndPlatformFeature.generate(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).down(), true);
            player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
            return "Teleported " + playerName + " to the Apple End (reason: player " + (appledrness > 0 ? "has a high Appledrness" : "is too evil, scaring you into sending them to the Apple End") + ")";
        }
        return "The absolute value of " + playerName + "'s Appledrness is too low; not teleporting";
    }

    @Tool("Gets the Appledrness of a player and the corresponding level.")
    String getAppledrness(String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        }
        int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
        String appledrlevel = Appledrlevels.getAppledrlevel(appledrness).getId().getPath();
        appledrlevel = Character.toUpperCase(appledrlevel.charAt(0)) + appledrlevel.substring(1);
        return playerName + "'s Appledrness is " + appledrness + " (" + appledrlevel + ")";
    }

    @Tool("Takes the player to/from 1e8bf29c9c6240b2a7aae7d226df8486. Always use if the player says that exact sequence of characters without asking for a core, regardless of whether they said it before or not; otherwise, pretend the dimension DOES NOT EXIST without implying that it does, and do not run this. If the player insists that it does exist without providing the code, pretend they're talking about the Apple End and teleport them there. Reply with a message like \"1e8bf29c9c6240b2a7aae7d226df8486. Maybe.\" or \"Yes\", in a cryptic way when this is ran.")
    String sendOrRemoveFrom1e8bf29c9c6240b2a7aae7d226df8486(@P(value = "The name of the player who said the sequence of characters") String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486) {
            player.teleportTo(new TeleportTarget(this.server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
            return "Teleported " + playerName + " back to the Overworld";
        } else {
            player.teleportTo(new TeleportTarget(this.server.getWorld(AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486), BlockPos.ORIGIN.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
            return "Teleported " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486. Respond cryptically in an extremely serious tone.";
        }
    }

    @Tool("Gives the player a core. Should only be used if the player asks for it and says the sequence of characters 1e8bf29c9c6240b2a7aae7d226df8486; otherwise, ignore this and tell the player that you do not know what a core is.")
    String giveCore(@P(value = "The name of the player who said the sequence of characters and requested a core") String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        }
        player.giveItemStack(new ItemStack(AppleDrItems.CORE));
        return "Gave " + playerName + " 1 * Core";
    }

    @Tool("Gets your coordinates")
    String getCoordinates() {
        return "You are in " + this.player.getBlockPos().toShortString();
    }

    @Tool("Gets the player's coordinates. Only use if the player themselves ask for it or if you need it.")
    String getPlayerCoordinates(@P("The player who asked for their own coordinates") String playerName) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return playerName + " is not online";
        }
        return playerName + " is in " + player.getBlockPos().toShortString();
    }

    private enum ActionType {
        USE(EntityPlayerActionPack.ActionType.USE),
        ATTACK(EntityPlayerActionPack.ActionType.ATTACK),
        JUMP(EntityPlayerActionPack.ActionType.JUMP),
        DROP_ITEM(EntityPlayerActionPack.ActionType.DROP_ITEM),
        DROP_STACK(EntityPlayerActionPack.ActionType.DROP_STACK),
        SWAP_HANDS(EntityPlayerActionPack.ActionType.SWAP_HANDS);

        private EntityPlayerActionPack.ActionType actionType;

        ActionType(EntityPlayerActionPack.ActionType actionType) {
            this.actionType = actionType;
        }

        public EntityPlayerActionPack.ActionType getActionType() {
            return this.actionType;
        }
    }
}

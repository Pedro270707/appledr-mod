package net.pedroricardo.util;

import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.sashirestela.openai.common.function.FunctionDef;
import io.github.sashirestela.openai.common.function.Functional;
import io.github.sashirestela.slimvalidator.constraints.Range;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import net.pedroricardo.AppleDrMod;
import net.pedroricardo.appledrness.Appledrness;
import net.pedroricardo.content.AppleDrDimension;
import net.pedroricardo.content.AppleDrItems;
import net.pedroricardo.content.AppleDrStatistics;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class PlayerAITools extends AITools {
    public static EntityPlayerMPFake player;
    public static MinecraftServer server;

    public PlayerAITools(EntityPlayerMPFake pPlayer, MinecraftServer pServer) {
        player = pPlayer;
        server = pServer;
    }

    private static class DoActionOnce implements Functional {
        @JsonPropertyDescription("The action: USE, ATTACK, JUMP, DROP_ITEM, DROP_STACK, or SWAP_HANDS")
        @JsonProperty(required = true)
        EntityPlayerActionPack.ActionType actionType;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().start(actionType, EntityPlayerActionPack.Action.once());
            AppleDrMod.LOGGER.info("Running action " + actionType + " on " + PlayerAITools.player.getGameProfile().getName() + " (o)");
            return "Running action " + actionType + " once";
        }
    }

    private static class DoActionContinuously implements Functional {
        @JsonPropertyDescription("The action: USE, ATTACK, JUMP, DROP_ITEM, DROP_STACK, or SWAP_HANDS")
        @JsonProperty(required = true)
        EntityPlayerActionPack.ActionType actionType;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().start(actionType, EntityPlayerActionPack.Action.continuous());
            AppleDrMod.LOGGER.info("Running action " + actionType + " on " + PlayerAITools.player.getGameProfile().getName() + " (c)");
            return "Running action " + actionType + " continuously";
        }
    }

    private static class DoActionForInterval implements Functional {
        @JsonPropertyDescription("The action: USE, ATTACK, JUMP, DROP_ITEM, DROP_STACK, or SWAP_HANDS")
        @JsonProperty(required = true)
        EntityPlayerActionPack.ActionType actionType;

        @JsonPropertyDescription("The interval in ticks")
        @JsonProperty(required = true)
        int interval;

        @JsonPropertyDescription("The interval offset in ticks")
        @JsonProperty(required = true)
        int offset;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().start(actionType, EntityPlayerActionPack.Action.interval(interval, offset));
            AppleDrMod.LOGGER.info("Running action " + actionType.name() + " on " + PlayerAITools.player.getGameProfile().getName() + " (interval: " + interval + ", offset: " + offset + ")");
            return "Running action " + actionType + " for an interval of " + interval + (offset <= 0 ? "" : " with an offset of " + offset);
        }
    }

    private static class SetSelectedSlot implements Functional {
        @JsonPropertyDescription("Slot index, between 0 and 8")
        @JsonProperty(required = true)
        @Range(min = 0, max = 8)
        int slot;

        @Override
        public Object execute() {
            slot = slot % 9 + 1;
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().setSlot(slot);
            AppleDrMod.LOGGER.info("Setting " + PlayerAITools.player.getGameProfile().getName() + "'s slot to " + slot + " (1-9)");
            return "Set slot to " + (slot - 1);
        }
    }

    private static class SetSneaking implements Functional {
        @JsonProperty(required = true)
        boolean sneaking;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().setSneaking(sneaking);
            AppleDrMod.LOGGER.info("Setting " + PlayerAITools.player.getGameProfile().getName() + "'s sneaking state to " + sneaking);
            return "Set sneaking to " + sneaking;
        }
    }

    private static class SetSprinting implements Functional {
        @JsonProperty(required = true)
        boolean sprinting;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().setSprinting(sprinting);
            AppleDrMod.LOGGER.info("Setting " + PlayerAITools.player.getGameProfile().getName() + "'s sprinting state to " + sprinting);
            return "Set sprinting to " + sprinting;
        }
    }

    private static class StopEverything implements Functional {
        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().stopAll();
            AppleDrMod.LOGGER.info("Stopping " + PlayerAITools.player.getGameProfile().getName() + "'s actions");
            return "Stopping every action!";
        }
    }

    private static class StopMovement implements Functional {
        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().stopMovement();
            AppleDrMod.LOGGER.info("Stopping " + PlayerAITools.player.getGameProfile().getName() + "'s movement");
            return "Movement stopped!";
        }
    }

    private static class LookInDirection implements Functional {
        @JsonPropertyDescription("The direction to look in")
        @JsonProperty(required = true)
        Direction direction;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().look(direction);
            AppleDrMod.LOGGER.info("Setting " + PlayerAITools.player.getGameProfile().getName() + "'s look direction to " + direction.getName());
            return "Looking at " + direction.getName();
        }
    }

    private static class LookAtPosition implements Functional {
        @JsonPropertyDescription("The X position")
        @JsonProperty(required = true)
        double x;

        @JsonPropertyDescription("The Y position")
        @JsonProperty(required = true)
        double y;

        @JsonPropertyDescription("The Z position")
        @JsonProperty(required = true)
        double z;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().lookAt(new Vec3d(x, y, z));
            AppleDrMod.LOGGER.info("Setting " + PlayerAITools.player.getGameProfile().getName() + "'s look direction to coordinate " + x + ", " + y + ", " + z);
            return "Looking at x=" + x + "; y=" + y + "; z=" + z;
        }
    }

    private static class LookAtPlayer implements Functional {
        @JsonPropertyDescription("The stackName of the player to look at")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to look at offline player " + playerName);
                return playerName + " is not online";
            }
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(player));
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " looked at " + playerName);
            return "Looked at " + playerName;
        }
    }

    private static class TurnInDegrees implements Functional {
        @JsonPropertyDescription("The yaw in degrees")
        @JsonProperty(required = true)
        float yaw;

        @JsonPropertyDescription("The pitch in degrees")
        @JsonProperty(required = true)
        float pitch;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().turn(yaw, pitch);
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " turned " + yaw + "° in yaw and " + pitch + "° in pitch");
            return "Turned " + yaw + "° in yaw and " + pitch + "° in pitch";
        }
    }

    private static class SetForward implements Functional {
        @JsonPropertyDescription("The amount from -1.0 to 1.0, with -1 being backward and 1 being forward")
        @JsonProperty(required = true)
        float amount;

        @Override
        public Object execute() {
            amount = MathHelper.clamp(amount, -1.0f, 1.0f);
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().setForward(amount);
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " is moving " + (amount < 0.0f ? "backward" : "forward") + " (" + amount + ")");
            return "Moving " + (amount < 0.0f ? "backward" : "forward");
        }
    }

    private static class SetStrafing implements Functional {
        @JsonPropertyDescription("The amount from -1.0 to 1.0, with -1 being right and 1 being left")
        @JsonProperty(required = true)
        float amount;

        @Override
        public Object execute() {
            amount = MathHelper.clamp(amount, -1.0f, 1.0f);
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().setStrafing(amount);
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " is moving " + (amount < 0.0f ? "right" : "left") + " (" + amount + ")");
            return "Moving " + (amount < 0.0f ? "right" : "left");
        }
    }

    private static class GiveAppledrnessToPlayer implements Functional {
        @JsonPropertyDescription("The stackName of the player to give Appledrness to")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to grace offline player " + playerName + " with Appledrness");
                return playerName + " is not online";
            } else if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(AppleDrStatistics.APPLEDRS_GRACE)) > 0) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to grace " + playerName + " with Appledrness again");
                return playerName + " cannot receive another Appledrness boost";
            } else {
                player.incrementStat(AppleDrStatistics.APPLEDRS_GRACE);
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " gave " + playerName + " an Appledrness grace");
                return "Gave " + playerName + " +50 Appledrness";
            }
        }
    }

    private static class TeleportToPlayer implements Functional {
        @JsonPropertyDescription("The stackName of the player to teleport to")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to teleport to offline player " + playerName);
                return playerName + " is not online";
            } else if (player.getBlockStateAtPos().isOf(Blocks.LAVA)) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to teleport to " + playerName + ", but they are in lava");
                return playerName + " is in lava";
            } else {
                PlayerAITools.player.teleportTo(new TeleportTarget(player.getServerWorld(), player.getPos(), Vec3d.ZERO, player.getYaw(), player.getPitch(), TeleportTarget.NO_OP));
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " teleported to " + playerName);
                return "Teleported to " + playerName;
            }
        }
    }

    private static class GetInventoryItems implements Functional {
        @Override
        public Object execute() {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < PlayerAITools.player.getInventory().size(); i++) {
                ItemStack stack = PlayerAITools.player.getInventory().getStack(i);
                if (stack.isEmpty()) continue;
                String indexText;
                if (i == 40) {
                    indexText = "[OFFHAND]: ";
                } else if (i >= 36) {
                    indexText = switch (i) {
                        case 39 -> String.format("[%s (HEAD)]: ", i);
                        case 38 -> String.format("[%s (BODY)]: ", i);
                        case 37 -> String.format("[%s (LEGS)]: ", i);
                        default -> String.format("[%s (FEET)]: ", i);
                    };
                } else if (PlayerAITools.player.getMainHandStack() == stack) {
                    indexText = String.format("[%s (MAINHAND)]: ", i);
                } else {
                    indexText = i < 9 ? String.format("[%s (HOTBAR)]: ", i) : String.format("[%s]: ", i);
                }

                StringBuilder tooltip = new StringBuilder();
                List<Text> lines = stack.getTooltip(Item.TooltipContext.DEFAULT, PlayerAITools.player, TooltipType.BASIC);
                for (int line = 0; line < lines.size(); line++) {
                    String translated = Localization.text(lines.get(line), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
                    if (translated.isEmpty()) continue;
                    tooltip.append(translated);
                    if (line == 0 && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        tooltip.append(" (").append(Localization.text(stack.getItem().getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString()).append(")");
                    }
                    tooltip.append(" / ");
                }

                str.append(indexText);
                if (stack.getCount() > 1) {
                    str.append("(").append(stack.getCount()).append(") ");
                }

                str.append(tooltip.length() >= 3 ? tooltip.substring(0, tooltip.length() - 3) : tooltip.toString()).append("; ");
            }
            String inventory = str.length() >= 2 ? str.substring(0, str.length() - 2) : str.toString();
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " checked their inventory: \n" + inventory);
            return "Your inventory: " + inventory;
        }
    }

    private static class DropStack implements Functional {
        @JsonPropertyDescription("This is the slot where the item currently is (the item you want to drop). Imagine it as \"Where is the item right now?\"")
        @JsonProperty(required = true)
        int slot;

        @JsonPropertyDescription("Whether to drop all (true) or just one (false)")
        @JsonProperty(required = true)
        boolean dropAll;

        @Override
        public Object execute() {
            ((ServerPlayerInterface) PlayerAITools.player).getActionPack().drop(slot, dropAll);
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " dropped item in slot " + slot);
            return "Dropping item in slot " + slot + "\n" + new GetInventoryItems().execute();
        }
    }

    private static class DropStackById implements Functional {
        @JsonPropertyDescription("The item's ID (e.g., `minecraft:netherite_boots`, `netherite_boots`, or `Netherite Boots`)")
        @JsonProperty(required = true)
        String id;

        @JsonPropertyDescription("Whether to drop all (true) or just one (false)")
        @JsonProperty(required = true)
        boolean dropAll;

        @Override
        public Object execute() {
            DropStack dropStack = new DropStack();
            dropStack.dropAll = dropAll;
            OptionalInt contains = OptionalInt.empty();
            for (int i = 0; i < PlayerAITools.player.getInventory().size(); i++) {
                ItemStack stack = PlayerAITools.player.getInventory().getStack(i);
                Identifier identifier = Identifier.tryParse(id);
                String stackName = Localization.text(stack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
                String itemName = Localization.text(stack.getItem().getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
                if (Registries.ITEM.getId(stack.getItem()).equals(identifier) || stackName.equals(id) || itemName.equals(id)) {
                    dropStack.slot = i;
                    return dropStack.execute();
                } else if (stackName.contains(id) || itemName.contains(id)) {
                    contains = OptionalInt.of(i);
                } else if (contains.isEmpty() && (stackName + " (" + itemName + ")").contains(id)) {
                    contains = OptionalInt.of(i);
                }
            }
            if (contains.isPresent()) {
                dropStack.slot = contains.getAsInt();
                return dropStack.execute();
            }
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to drop " + id + ", but the item does not exist or could not be found");
            return "Could not find " + id;
        }
    }

    private static class MoveStack implements Functional {
        private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

        @JsonPropertyDescription("The target slot where you want to move the item to. Think of it as \"Where do you want the item to go?\"")
        @JsonProperty(required = true)
        int to;

        @JsonPropertyDescription("This is the slot where the item currently is (the item you want to move). Imagine it as \"Where is the item right now?\"")
        @JsonProperty(required = true)
        int from;

        @Override
        public Object execute() {
            if (from < 0 || from > PlayerAITools.player.getInventory().size()) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to move item in non-existent slot " + from + " to slot " + to);
                return "Origin slot does not exist!";
            }
            if (to < 0 || to > PlayerAITools.player.getInventory().size()) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to move item in slot " + from + " to non-existent slot " + to);
                return "Target slot does not exist!";
            }

            ItemStack originStack = PlayerAITools.player.getInventory().getStack(from).copyAndEmpty();
            ItemStack targetStack = PlayerAITools.player.getInventory().getStack(to).copyAndEmpty();

            if (to < PlayerAITools.player.getInventory().main.size() || to >= PlayerAITools.player.getInventory().main.size() + PlayerAITools.player.getInventory().armor.size() || EQUIPMENT_SLOTS[to - PlayerAITools.player.getInventory().main.size()] == PlayerAITools.player.getPreferredEquipmentSlot(originStack)) {
                PlayerAITools.player.getInventory().setStack(to, originStack);
            } else {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to move " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " (slot " + from + ") to slot " + to + ", but it was not accepted");
                return "Slot " + to + " does not accept " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
            }

            if (from < PlayerAITools.player.getInventory().main.size() || from >= PlayerAITools.player.getInventory().main.size() + PlayerAITools.player.getInventory().armor.size() || EQUIPMENT_SLOTS[from - PlayerAITools.player.getInventory().main.size()] == PlayerAITools.player.getPreferredEquipmentSlot(targetStack)) {
                PlayerAITools.player.getInventory().setStack(from, targetStack);
            } else if (!PlayerAITools.player.giveItemStack(targetStack)) {
                PlayerAITools.player.dropStack(targetStack);
            }

            if (targetStack.isEmpty()) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " moved " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " (slot " + from + ") to slot " + to);
                return "Moved " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " to slot " + to + "\n" + new GetInventoryItems().execute();
            } else {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " switched " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " (slot " + from + ") with " + Localization.text(targetStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " (slot " + to + ")");
                return "Switched " + Localization.text(originStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + " with " + Localization.text(targetStack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString() + "\n" + new GetInventoryItems().execute();
            }
        }
    }

    private static class MoveStackById implements Functional {
        @JsonPropertyDescription("The target slot")
        @JsonProperty(required = true)
        int target;

        @JsonPropertyDescription("The item's ID (e.g., `minecraft:netherite_boots`, `netherite_boots`, or `Netherite Boots`)")
        @JsonProperty(required = true)
        String id;

        @Override
        public Object execute() {
            MoveStack moveStack = new MoveStack();
            moveStack.to = target;
            OptionalInt contains = OptionalInt.empty();
            for (int i = 0; i < PlayerAITools.player.getInventory().size(); i++) {
                if (i == target) continue;
                ItemStack stack = PlayerAITools.player.getInventory().getStack(i);
                Identifier identifier = Identifier.tryParse(id);
                String stackName = Localization.text(stack.getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
                String itemName = Localization.text(stack.getItem().getName(), ServerLanguage.getLanguage(Language.DEFAULT_LANGUAGE)).getString();
                if (Registries.ITEM.getId(stack.getItem()).equals(identifier) || stackName.equals(id) || itemName.equals(id)) {
                    moveStack.from = i;
                    return moveStack.execute();
                } else if (stackName.contains(id) || itemName.contains(id)) {
                    contains = OptionalInt.of(i);
                } else if (contains.isEmpty() && (stackName + " (" + itemName + ")").contains(id)) {
                    contains = OptionalInt.of(i);
                }
            }
            if (contains.isPresent()) {
                moveStack.from = contains.getAsInt();
                return moveStack.execute();
            }
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to move " + id + " to slot " + target + ", but the item does not exist or could not be found");
            return "Could not find " + id;
        }
    }

    private static class SendOrRemoveFromAppleEnd implements Functional {
        @JsonPropertyDescription("The stackName of the player who asked to be teleported")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            int appledrness;
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to teleport offline player " + playerName + " to the Apple End");
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " teleported " + playerName + " from the Apple End to the Overworld");
                player.teleportTo(new TeleportTarget(server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else if (Math.abs(appledrness = Appledrness.getAppledrness(player.getWorld(), player)) >= AppleDrConfig.appleEndAppledrness) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " teleported " + playerName + " to the Apple End");
                EndPlatformFeature.generate(server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).down(), true);
                player.teleportTo(new TeleportTarget(server.getWorld(AppleDrDimension.WORLD), BlockPos.ORIGIN.add(0, 60, 0).toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " to the Apple End (reason: player " + (appledrness > 0 ? "has a high Appledrness" : "is too evil, scaring you into sending them to the Apple End") + ")";
            }
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to teleport " + playerName + " to the Apple End, but their Appledrness is too low");
            return "The absolute value of " + playerName + "'s Appledrness is too low; not teleporting";
        }
    }

    private static class GetAppledrness implements Functional {
        @JsonPropertyDescription("The stackName of the player to give Appledrness to")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to check the Appledrness of offline player " + playerName);
                return playerName + " is not online";
            }
            int appledrness = Appledrness.getAppledrness(player.getWorld(), player);
            String appledrlevel = Appledrlevels.getAppledrlevel(appledrness).getId().getPath();
            appledrlevel = Character.toUpperCase(appledrlevel.charAt(0)) + appledrlevel.substring(1);
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " checked the Appledrness of " + playerName + " (" + appledrness + ", " + appledrlevel + ")");
            return playerName + "'s Appledrness is " + appledrness + " (" + appledrlevel + ")";
        }
    }
    
    private static class SendOrRemoveFrom1e8bf29c9c6240b2a7aae7d226df8486 implements Functional {
        @JsonPropertyDescription("The stackName of the player who said the sequence of characters")
        @JsonProperty(required = true)
        String playerName;
        
        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to teleport offline player " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486");
                return playerName + " is not online";
            } else if (player.getWorld().getRegistryKey() == AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " teleported " + playerName + " from 1e8bf29c9c6240b2a7aae7d226df8486 to the Overworld");
                player.teleportTo(new TeleportTarget(server.getWorld(World.OVERWORLD), player, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)));
                return "Teleported " + playerName + " back to the Overworld";
            } else {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " teleported " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486");
                player.teleportTo(new TeleportTarget(server.getWorld(AppleDrDimension.WORLD_1E8BF29C9C6240B2A7AAE7D226DF8486), BlockPos.ORIGIN.toCenterPos(), Vec3d.ZERO, 0.0f, 0.0f, TeleportTarget.NO_OP));
                return "Teleported " + playerName + " to 1e8bf29c9c6240b2a7aae7d226df8486. Respond cryptically in an extremely serious tone.";
            }
        }
    }

    private static class GiveCore implements Functional {
        @JsonPropertyDescription("The stackName of the player who said the sequence of characters")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to give offline player " + playerName + " a core");
                return playerName + " is not online";
            }
            player.giveItemStack(new ItemStack(AppleDrItems.CORE));
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " gave offline player " + playerName + " a core");
            return "Gave " + playerName + " 1 * Core";
        }
    }

    private static class GetOwnCoordinates implements Functional {
        @Override
        public Object execute() {
            AppleDrMod.LOGGER.info(player.getGameProfile().getName() + " fetched their coordinates: " + player.getBlockPos().toShortString());
            return "You are in " + player.getBlockPos().toShortString();
        }
    }

    private static class GetPlayerCoordinates implements Functional {
        @JsonPropertyDescription("The player who asked for their own coordinates")
        @JsonProperty(required = true)
        String playerName;

        @Override
        public Object execute() {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player == null) {
                AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " tried to get the coordinates of offline player " + playerName);
                return playerName + " is not online";
            }
            AppleDrMod.LOGGER.info(PlayerAITools.player.getGameProfile().getName() + " got the coordinates of " + playerName + " (" + player.getBlockPos().toShortString() + ")");
            return playerName + " is in " + player.getBlockPos().toShortString();
        }
    }

    @Override
    public List<FunctionDef> getTools() {
        List<FunctionDef> list = new ArrayList<>();
        list.add(FunctionDef.builder()
                .name("do_action_once")
                .description("Does an action (USE, ATTACK, JUMP, DROP ITEM(S), DROP STACK(S), or SWAP HANDS) once.")
                .functionalClass(DoActionOnce.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("do_action_continuously")
                .description("Does an action (USE, ATTACK, JUMP, DROP ITEM(S), DROP STACK(S), or SWAP HANDS) continuously.")
                .functionalClass(DoActionContinuously.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("do_action_for_interval")
                .description("Does an action (USE, ATTACK, JUMP, DROP ITEM(S), DROP STACK(S), or SWAP HANDS) for an interval of time.")
                .functionalClass(DoActionForInterval.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("set_selected_slot")
                .description("Sets your selected hotbar slot from 0 to 8.")
                .functionalClass(SetSelectedSlot.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("set_sneaking")
                .description("Sets if you are sneaking or not.")
                .functionalClass(SetSneaking.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("set_sprinting")
                .description("Sets if you are sprinting or not.")
                .functionalClass(SetSprinting.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("stop_everything")
                .description("Stops every action you are doing.")
                .functionalClass(StopEverything.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("stop_movement")
                .description("Stops your movement.")
                .functionalClass(StopMovement.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("look_in_direction")
                .description("Looks in a direction.")
                .functionalClass(LookInDirection.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("look_at_position")
                .description("Looks at a position.")
                .functionalClass(LookAtPosition.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("look_at_player")
                .description("Looks at a player.")
                .functionalClass(LookAtPlayer.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("turn_in_degrees")
                .description("Turns your camera the given amount of degrees.")
                .functionalClass(TurnInDegrees.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("set_forward")
                .description("Moves forward (or backward if negative).")
                .functionalClass(SetForward.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("set_strafing")
                .description("Moves to the left (or right if negative).")
                .functionalClass(SetStrafing.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("give_appledrness_to_player")
                .description("Gives the player 50 Appledrness if they haven't received it yet. Respond according to the received Appledrness.")
                .functionalClass(GiveAppledrnessToPlayer.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("teleport_to_player")
                .description("Teleports to the player unless they are in a dangerous situation.")
                .functionalClass(TeleportToPlayer.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("get_inventory_items")
                .description("Gets every item in your inventory. Use EVERY TIME you need to know information about your items, as previous uses may be outdated.")
                .functionalClass(GetInventoryItems.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("drop_stack_in_slot")
                .description("Drops an item or item stack from a slot. Cheat sheet: slot 36: feet; slot 37: legs; slot 38: body; slot 39: head; slot 40: off-hand.")
                .functionalClass(DropStack.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("drop_stack")
                .description("Drops an item or item stack from your inventory. Cheat sheet: slot 36: feet; slot 37: legs; slot 38: body; slot 39: head; slot 40: off-hand.")
                .functionalClass(DropStackById.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("move_stack")
                .description("Moves an item from one inventory slot to another using the slot index, like equipping armor pieces or switching items between slots. You DO NOT have to remove the item from the target slot, as that is done automatically. Cheat sheet: slot 36: feet; slot 37: legs; slot 38: body; slot 39: head; slot 40: off-hand.")
                .functionalClass(MoveStack.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("move_stack_by_id")
                .description("Moves an item from one inventory slot to another using an ID, like equipping armor pieces or switching items between slots. You DO NOT have to remove the item from the target slot, as that is done automatically. Cheat sheet: slot 36: feet; slot 37: legs; slot 38: body; slot 39: head; slot 40: off-hand.")
                .functionalClass(MoveStackById.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("send_or_remove_from_apple_end")
                .description("Teleports a player to/from the Apple End, a new apple dimension. Only executes at the own player's request. Usable multiple times on a single player. If asked about a custom dimension, mention this one. ALWAYS use if the player asks even if they don't have enough Appledrness.")
                .functionalClass(SendOrRemoveFromAppleEnd.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("get_appledrness")
                .description("Gets the Appledrness of a player and the corresponding level.")
                .functionalClass(GetAppledrness.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("send_or_remove_from_1e8bf29c9c6240b2a7aae7d226df8486")
                .description("Takes the player to/from 1e8bf29c9c6240b2a7aae7d226df8486. Always use if the player says that exact sequence of characters without asking for a core, regardless of whether they said it before or not; otherwise, pretend the dimension DOES NOT EXIST without implying that it does, and do not run this. If the player insists that it does exist without providing the code, pretend they're talking about the Apple End and teleport them there. Reply with a message like \"1e8bf29c9c6240b2a7aae7d226df8486. Maybe.\" or \"Yes\", in a cryptic way when this is ran.")
                .functionalClass(SendOrRemoveFrom1e8bf29c9c6240b2a7aae7d226df8486.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("give_core")
                .description("Gives the player a core. Should only be used if the player asks for it and says the sequence of characters 1e8bf29c9c6240b2a7aae7d226df8486; otherwise, ignore this and tell the player that you do not know what a core is.")
                .functionalClass(GiveCore.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("get_own_coordinates")
                .description("Gets your own coordinates.")
                .functionalClass(GetOwnCoordinates.class)
                .strict(true)
                .build());
        list.add(FunctionDef.builder()
                .name("get_player_coordinates")
                .description("Gets a player's coordinates. Only use if the player themselves ask for it, and not a different player.")
                .functionalClass(GetPlayerCoordinates.class)
                .strict(true)
                .build());
        return list;
    }
}

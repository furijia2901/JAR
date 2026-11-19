package jp.worldjourney.gachafx;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.Random;

/** Standalone client-only animation viewer. No item consumption, no rewards, no World Journey dependency. */
public final class GachaFxClient implements ClientModInitializer {
    private static final Random RANDOM = new Random();
    private static int cursor = 0;
    public static final GachaFxPlayer PLAYER = new GachaFxPlayer();

    /** Integration entrypoint: client-side code in another mod may invoke this method. */
    public static boolean play(String presetId, String itemName, String rarity) {
        GachaFxLibrary.Preset preset = GachaFxLibrary.get(presetId);
        if (preset == null) return false;
        PLAYER.play(preset, itemName, rarity);
        return true;
    }
    @Override public void onInitializeClient() {
        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath("gachafx", "overlay"), PLAYER::render);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("gachafx")
                .executes(ctx -> { report(ctx.getSource(), "ガチャ演出スタジオ: 60種類。 /gachafx random | next | list | play <id> | stop"); return 1; })
                .then(ClientCommands.literal("random").executes(ctx -> {
                    var p = GachaFxLibrary.random(RANDOM);
                    play(p.id(), "PREVIEW REWARD", "SSR"); report(ctx.getSource(), "再生: " + p.id()); return 1;
                }))
                .then(ClientCommands.literal("next").executes(ctx -> {
                    var p = GachaFxLibrary.byIndex(cursor++);
                    play(p.id(), "PREVIEW REWARD", "SSR"); report(ctx.getSource(), "再生 [" + cursor + "/60]: " + p.id()); return 1;
                }))
                .then(ClientCommands.literal("stop").executes(ctx -> { PLAYER.stop(); report(ctx.getSource(), "演出停止"); return 1; }))
                .then(ClientCommands.literal("list").executes(ctx -> {
                    report(ctx.getSource(), "テーマ: celestial/sakura/abyss/solar/ocean");
                    report(ctx.getSource(), "動き: " + String.join(", ", GachaFxLibrary.MOTIONS));
                    report(ctx.getSource(), "指定例: /gachafx play celestial_portal"); return 1;
                }))
                .then(ClientCommands.literal("play")
                    .then(ClientCommands.argument("id", StringArgumentType.word()).suggests((ctx, b) -> {
                        for (var p : GachaFxLibrary.ALL) b.suggest(p.id());
                        return b.buildFuture();
                    }).executes(ctx -> {
                        String id = StringArgumentType.getString(ctx, "id");
                        if (!play(id, "PREVIEW REWARD", "SSR")) { report(ctx.getSource(), "不明なID: " + id); return 0; }
                        report(ctx.getSource(), "再生: " + id); return 1;
                    }))));
        });
    }
    private static void report(net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource src, String msg) {
        src.sendFeedback(Component.literal("[GachaFX] " + msg));
    }
}

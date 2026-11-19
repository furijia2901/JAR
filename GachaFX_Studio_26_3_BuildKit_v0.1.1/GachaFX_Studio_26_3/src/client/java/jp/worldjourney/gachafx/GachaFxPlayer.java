package jp.worldjourney.gachafx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import java.util.Locale;

/** Animated cinematic overlay: intro, charge, reveal, outro with twelve different particle trajectories. */
public final class GachaFxPlayer {
    private GachaFxLibrary.Preset preset;
    private long startNanos;
    private String prize = "PREVIEW REWARD", rarity = "SSR";
    public void play(GachaFxLibrary.Preset chosen, String reward, String grade) {
        preset = chosen;
        prize = sanitize(reward, "PREVIEW REWARD");
        rarity = sanitize(grade, "SSR");
        startNanos = System.nanoTime();
    }
    private String sanitize(String s, String fallback) {
        return s == null || s.isBlank() ? fallback : s.substring(0, Math.min(s.length(), 48));
    }
    public void stop() { preset = null; }
    public void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        GachaFxLibrary.Preset p = preset;
        if (p == null) return;
        float sec = (System.nanoTime() - startNanos) / 1_000_000_000f;
        float duration = p.durationTicks() / 20f;
        if (sec >= duration) { preset = null; return; }
        int w = g.guiWidth(), h = g.guiHeight(), cx = w / 2, cy = h / 2;
        var theme = p.theme();
        float intro = clamp(sec / 0.48f), outro = clamp((duration - sec) / .65f);
        float opacity = Math.min(intro, outro);
        // Black-tinted cinematic wash plus colored letterbox strips.
        rect(g, 0, 0, w, h, argb(0x030610, (int)(205 * opacity)));
        rect(g, 0, 0, w, 14, argb(theme.base(), (int)(245 * opacity)));
        rect(g, 0, h-14, w, h, argb(theme.base(), (int)(245 * opacity)));
        int particleCount = 96;
        float phase = sec * 3.5f;
        float charge = clamp((sec - .4f)/1.7f);
        boolean revealed = sec >= 2.55f;
        // Easing, deterministic phases and hashed particles: 12 genuinely different motion paths.
        for (int i = 0; i < particleCount; i++) {
            double seed = fract(Math.sin(i * 86.732 + p.id().hashCode() * 0.001) * 47358.2);
            double seed2 = fract(Math.sin(i * 13.471 + 1.7) * 43758.5);
            double a = 2*Math.PI * ((i + seed)/particleCount);
            double r = Math.min(w,h) * (.05 + .49*seed2);
            double x, y;
            switch (p.motion()) {
                case "burst" -> { double rr = r * Math.max(.05, (sec - .5)*.62); x = Math.cos(a)*rr; y = Math.sin(a)*rr; }
                case "orbit" -> { x = Math.cos(a + phase*(.25+seed))*r*.82; y = Math.sin(a + phase*(.25+seed))*r*.43; }
                case "rain" -> { x = (seed * 2 - 1)*w*.48; y = ((seed2 + sec*(.2+seed*.17))%1.0 - .5)*h; }
                case "portal" -> { double rr = (r*.25 + (sec * 24 + i*5)%Math.max(30,r)); x = Math.cos(a + phase)*rr; y = Math.sin(a + phase)*rr*.7; }
                case "shatter" -> { double rr = Math.max(0,sec-1.0)*(25+r); x = Math.cos(a)*rr; y = Math.sin(a)*rr; }
                case "spiral" -> { double rr = r*(.2+.8*(sec%2.1)/2.1); x = Math.cos(a+phase*1.5+rr*.03)*rr; y = Math.sin(a+phase*1.5+rr*.03)*rr; }
                case "pulse" -> { double rr = r*(.55+.4*Math.sin(phase*2+a)); x = Math.cos(a)*rr; y = Math.sin(a)*rr; }
                case "comet" -> { x = -w*.55 + (sec*(75+seed*65)+seed2*w*1.2)%(w*1.6); y = -h*.40 + (seed*h*.8) + Math.sin(phase+i)*14; }
                case "constellation" -> { x = Math.cos(a)*(r*.95); y = Math.sin(a)*(r*.57); }
                case "roulette" -> { double rr = r*.7; x = Math.cos(a+phase*2)*rr; y = Math.sin(a+phase*2)*rr*.36; }
                case "wave" -> { x = (seed*2-1)*w*.5; y = Math.sin((seed*8)+phase*2)*r*.45 + (seed2-.5)*h*.3; }
                case "ascend" -> { x = (seed*2-1)*w*.45 + Math.sin(phase+i)*15; y = (0.5-((seed2 + sec*.18*(.7+seed))%1.0))*h; }
                default -> { x = Math.cos(a)*r; y = Math.sin(a)*r; }
            }
            int px = cx+(int)x, py=cy+(int)y;
            int alpha = (int)(opacity * (95 + 145*seed));
            int color = argb((i%7==0 ? theme.highlight() : theme.accent()), alpha);
            int size = i%13==0 ? 4 : (i%4==0 ? 3 : 2);
            rect(g, px, py, px+size, py+size, color);
            if (i%11==0) { rect(g, px-4, py+1, px+size+4, py+2, argb(theme.highlight(), alpha/3)); }
        }
        // A six-ring core that contracts during charging and expands on reveal.
        int core = (int)(12 + 25*charge + (revealed ? 14*Math.exp(-(sec-2.55)*2) : 0));
        for(int ring=5; ring>=0; ring--) {
            double rr = core + ring*11 + Math.sin(phase*2+ring)*3;
            int col = argb(ring%2==0 ? theme.accent() : theme.highlight(), (int)(opacity*(32+ring*11)));
            ring(g, cx, cy, (int)rr, col);
        }
        if (revealed) {
            float pop = clamp((sec-2.55f)/.45f);
            int bandHalf = (int)(Math.min(w*.46, 140+pop*55));
            rect(g, cx-bandHalf, cy-35, cx+bandHalf, cy+45, argb(theme.base(), (int)(210*opacity*pop)));
            rect(g, cx-bandHalf, cy-35, cx+bandHalf, cy-32, argb(theme.highlight(), (int)(255*opacity*pop)));
            drawCentered(g, rarity.toUpperCase(Locale.ROOT), cx, cy-22, argb(theme.highlight(),(int)(255*opacity)));
            drawCentered(g, prize, cx, cy+2, argb(0xFFFFFF,(int)(255*opacity)));
            drawCentered(g, "REVEALED", cx, cy+23, argb(theme.accent(),(int)(255*opacity)));
        } else {
            drawCentered(g, sec < 1.3 ? "SUMMONING" : "AWAKENING", cx, cy-8, argb(theme.highlight(),(int)(255*opacity)));
        }
        drawCentered(g, "GACHA FX STUDIO  /  " + p.id(), cx, h-28, argb(0xDFE4FF,(int)(220*opacity)));
    }
    private static void ring(GuiGraphicsExtractor g,int x,int y,int r,int color) {
        if(r<1)return;
        // Point-sampled circle: compatible with both Vulkan and OpenGL backends.
        for(int j=0;j<80;j++) {
            double a=j*Math.PI*2/80;
            int xx=x+(int)(Math.cos(a)*r), yy=y+(int)(Math.sin(a)*r*.73);
            rect(g,xx,yy,xx+2,yy+2,color);
        }
    }
    private static void drawCentered(GuiGraphicsExtractor g,String s,int cx,int y,int color) {
        var font=Minecraft.getInstance().font;
        g.drawString(font,s,cx-font.width(s)/2,y,color,true);
    }
    private static int argb(int rgb,int a) { return ((Math.max(0,Math.min(255,a)))<<24) | (rgb&0xFFFFFF); }
    private static float clamp(float t) {return Math.max(0,Math.min(1,t));}
    private static double fract(double x) {return x-Math.floor(x);}
    private static void rect(GuiGraphicsExtractor g,int x1,int y1,int x2,int y2,int c) { g.fill(x1,y1,x2,y2,c); }
}

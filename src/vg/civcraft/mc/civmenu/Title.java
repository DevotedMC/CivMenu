package vg.civcraft.mc.civmenu;

import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_11_R1.PlayerConnection;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {
	private String title;
	private String subtitle;
	private int fadeIn;
	private int stay;
	private int fadeOut;

	public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public int getFadeIn() {
		return fadeIn;
	}

	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}

	public int getStay() {
		return stay;
	}

	public void setStay(int stay) {
		this.stay = stay;
	}

	public int getFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
	}

	public void sendTitle(Player p) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		PacketPlayOutTitle packet = new PacketPlayOutTitle(
				PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay,
				fadeOut);
		connection.sendPacket(packet);
		IChatBaseComponent sub = IChatBaseComponent.ChatSerializer
				.a("{\"text\": \"" + subtitle + "\"}");
		packet = new PacketPlayOutTitle(
				PacketPlayOutTitle.EnumTitleAction.SUBTITLE, sub);
		connection.sendPacket(packet);
		IChatBaseComponent main = IChatBaseComponent.ChatSerializer
				.a("{\"text\": \"" + title + "\"}");
		packet = new PacketPlayOutTitle(
				PacketPlayOutTitle.EnumTitleAction.TITLE, main);
		connection.sendPacket(packet);
	}
}

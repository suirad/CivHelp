package vg.civcraft.mc.civhelp.civmenu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class Menu {
	TextComponent title;
	TextComponent subTitle;
	List<TextComponent> parts;
	private final static int CHAT_SIZE = 52;
	
	public Menu(){
		title = new TextComponent("");
		subTitle = new TextComponent("");
		parts = new ArrayList<TextComponent>();
	}
	
	/**
	 * Returns the title
	 * @return Returns the TextComponent of the title.
	 */
	public TextComponent getTitle() {
		return title;
	}

	public void setTitle(TextComponent title) {
		this.title = title;
	}

	public TextComponent getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(TextComponent subTitle) {
		this.subTitle = subTitle;
	}

	public List<TextComponent> getParts() {
		return parts;
	}

	public void addPart(TextComponent part){
		parts.add(part);
	}
	
	public void setParts(TextComponent... parts){
		for(TextComponent part:parts){
			this.parts.add(part);
		}
	}
	
	public void sendPlayer(Player p){
		if (p == null){return;}
		TextComponent formattedtitle = (TextComponent) this.title.duplicate();
		formattedtitle.setText(StringUtils.center(this.title.getText(), CHAT_SIZE, '-'));
		p.spigot().sendMessage(formattedtitle);
		
		if (!subTitle.equals("")){
			p.spigot().sendMessage(subTitle);
		}
		
		TextComponent message = new TextComponent("");
		int counter = CHAT_SIZE;
		for(int i = 0; i<parts.size(); i++){
			message.addExtra("[");
			message.addExtra(parts.get(i));
			message.addExtra("] ");
			counter -= (parts.get(i).getText().length() + 2);
			if(i + 1 != parts.size()){
				if(counter < parts.get(i+1).getText().length() + 2){
					p.spigot().sendMessage(message);
					message = new TextComponent("");
					counter = CHAT_SIZE;
				}
			} else {
				p.spigot().sendMessage(message);
			}
			
		}
		
		p.spigot().sendMessage(formattedtitle);
		
	}
}
